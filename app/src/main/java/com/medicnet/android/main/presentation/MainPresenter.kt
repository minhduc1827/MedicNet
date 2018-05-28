package com.medicnet.android.main.presentation

import chat.rocket.common.RocketChatAuthException
import chat.rocket.common.RocketChatException
import chat.rocket.common.model.UserStatus
import chat.rocket.common.util.ifNull
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.realtime.setDefaultStatus
import chat.rocket.core.internal.rest.logout
import chat.rocket.core.internal.rest.me
import chat.rocket.core.internal.rest.unregisterPushToken
import chat.rocket.core.model.Myself
import com.medicnet.android.core.lifecycle.CancelStrategy
import com.medicnet.android.infrastructure.LocalRepository
import com.medicnet.android.main.viewmodel.NavHeaderViewModel
import com.medicnet.android.main.viewmodel.NavHeaderViewModelMapper
import com.medicnet.android.server.domain.*
import com.medicnet.android.server.domain.model.Account
import com.medicnet.android.server.infraestructure.ConnectionManagerFactory
import com.medicnet.android.server.infraestructure.RocketChatClientFactory
import com.medicnet.android.server.presentation.CheckServerPresenter
import com.medicnet.android.util.LogUtil
import com.medicnet.android.util.extensions.launchUI
import com.medicnet.android.util.extensions.registerPushToken
import com.medicnet.android.util.extensions.serverLogoUrl
import com.medicnet.android.util.retryIO
import kotlinx.coroutines.experimental.channels.Channel
import timber.log.Timber
import javax.inject.Inject

class MainPresenter @Inject constructor(
    private val view: MainView,
    private val strategy: CancelStrategy,
    private val navigator: MainNavigator,
    private val tokenRepository: TokenRepository,
    private val serverInteractor: GetCurrentServerInteractor,
    private val localRepository: LocalRepository,
    private val navHeaderMapper: NavHeaderViewModelMapper,
    private val saveAccountInteractor: SaveAccountInteractor,
    private val getAccountsInteractor: GetAccountsInteractor,
    private val removeAccountInteractor: RemoveAccountInteractor,
    private val factory: RocketChatClientFactory,
    getSettingsInteractor: GetSettingsInteractor,
    managerFactory: ConnectionManagerFactory
) : CheckServerPresenter(strategy, factory, view = view) {
    private val currentServer = serverInteractor.get()!!
    private val manager = managerFactory.create(currentServer)
    private val client: RocketChatClient = factory.create(currentServer)
    private var settings: PublicSettings = getSettingsInteractor.get(serverInteractor.get()!!)

    private val userDataChannel = Channel<Myself>()

    fun toChatList() = navigator.toChatList()

    fun toUserProfile() = navigator.toUserProfile()

    fun toSettings() = navigator.toSettings()

    fun loadCurrentInfo() {
        checkServerInfo(currentServer)
        launchUI(strategy) {
            try {
                val me = retryIO("me") {
                    client.me()
                }
                val model = navHeaderMapper.mapToViewModel(me)
                saveAccount(model)
                LogUtil.d("MainPresenter", "loadCurrentInfo")
                view.setupNavHeader(model, getAccountsInteractor.get())
            } catch (ex: Exception) {
                when (ex) {
                    is RocketChatAuthException -> {
                        logout()
                    }
                    else -> {
                        Timber.d(ex, "Error loading my information for navheader")
                        ex.message?.let {
                            view.showMessage(it)
                        }.ifNull {
                            view.showGenericErrorMessage()
                        }
                    }
                }
            }
            subscribeMyselfUpdates()
        }
    }

    /**
     * Logout from current server.
     */
    fun logout() {
        launchUI(strategy) {
            try {
                clearTokens()
                retryIO("logout") { client.logout() }
            } catch (exception: RocketChatException) {
                Timber.d(exception, "Error calling logout")
                exception.message?.let {
                    view.showMessage(it)
                }.ifNull {
                    view.showGenericErrorMessage()
                }
            }

            try {
                disconnect()
                removeAccountInteractor.remove(currentServer)
                tokenRepository.remove(currentServer)
                navigator.toNewServer()
            } catch (ex: Exception) {
                Timber.d(ex, "Error cleaning up the session...")
            }

            navigator.toNewServer()
        }
    }

    fun connect() {
        manager.connect()
    }

    fun disconnect() {
        manager.removeUserDataChannel(userDataChannel)
        manager.disconnect()
    }

    fun changeServer(serverUrl: String) {
        if (currentServer != serverUrl) {
            navigator.toNewServer(serverUrl)
        } else {
            view.closeServerSelection()
        }
    }

    fun addNewServer() {
        navigator.toServerScreen()
    }

    fun changeDefaultStatus(userStatus: UserStatus) {
        launchUI(strategy) {
            try {
                client.setDefaultStatus(userStatus)
                view.showUserStatus(userStatus)
            } catch (ex: RocketChatException) {
                ex.message?.let {
                    view.showMessage(it)
                }.ifNull {
                    view.showGenericErrorMessage()
                }
            }
        }
    }

    suspend fun refreshToken(token: String?) {
        token?.let {
            localRepository.save(LocalRepository.KEY_PUSH_TOKEN, token)
            client.registerPushToken(token, getAccountsInteractor.get(), factory)
        }
    }

    private suspend fun saveAccount(viewModel: NavHeaderViewModel) {
        val icon = settings.favicon()?.let {
            currentServer.serverLogoUrl(it)
        }
        val account = Account(
            currentServer,
            icon,
            viewModel.serverLogo,
            viewModel.userDisplayName!!,
            viewModel.userAvatar
        )
        saveAccountInteractor.save(account)
    }

    private suspend fun clearTokens() {
        serverInteractor.clear()
        val pushToken = localRepository.get(LocalRepository.KEY_PUSH_TOKEN)
        if (pushToken != null) {
            try {
                retryIO("unregisterPushToken") { client.unregisterPushToken(pushToken) }
            } catch (ex: Exception) {
                Timber.d(ex, "Error unregistering push token")
            }
        }
        localRepository.clearAllFromServer(currentServer)
    }

    private suspend fun subscribeMyselfUpdates() {
        manager.addUserDataChannel(userDataChannel)
        for (myself in userDataChannel) {
            updateMyself(myself)
        }
    }

    private suspend fun updateMyself(myself: Myself) {
        LogUtil.d("MainPresenter", "updateMyself")
        val model = navHeaderMapper.mapToViewModel(myself)
        view.setupNavHeader(model, getAccountsInteractor.get())
    }
}