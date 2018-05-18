package com.medic.net.authentication.twofactor.presentation

import com.medic.net.authentication.presentation.AuthenticationNavigator
import com.medic.net.core.lifecycle.CancelStrategy
import com.medic.net.infrastructure.LocalRepository
import com.medic.net.server.domain.*
import com.medic.net.server.domain.model.Account
import com.medic.net.server.infraestructure.RocketChatClientFactory
import com.medic.net.util.extensions.avatarUrl
import com.medic.net.util.extensions.launchUI
import com.medic.net.util.extensions.registerPushToken
import com.medic.net.util.extensions.serverLogoUrl
import com.medic.net.util.retryIO
import chat.rocket.common.RocketChatAuthException
import chat.rocket.common.RocketChatException
import chat.rocket.common.util.ifNull
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.rest.login
import chat.rocket.core.internal.rest.me
import chat.rocket.core.model.Myself
import javax.inject.Inject

class TwoFAPresenter @Inject constructor(private val view: TwoFAView,
                                         private val strategy: CancelStrategy,
                                         private val navigator: AuthenticationNavigator,
                                         private val tokenRepository: TokenRepository,
                                         private val localRepository: LocalRepository,
                                         private val serverInteractor: GetCurrentServerInteractor,
                                         private val factory: RocketChatClientFactory,
                                         private val saveAccountInteractor: SaveAccountInteractor,
                                         private val getAccountsInteractor: GetAccountsInteractor,
                                         settingsInteractor: GetSettingsInteractor) {
    private val currentServer = serverInteractor.get()!!
    private val client: RocketChatClient = factory.create(currentServer)
    private var settings: PublicSettings = settingsInteractor.get(serverInteractor.get()!!)

    // TODO: If the usernameOrEmail and password was informed by the user on the previous screen, then we should pass only the pin, like this: fun authenticate(pin: EditText)
    fun authenticate(usernameOrEmail: String, password: String, twoFactorAuthenticationCode: String) {
        val server = serverInteractor.get()
        when {
            server == null -> {
                navigator.toServerScreen()
            }
            twoFactorAuthenticationCode.isBlank() -> {
                view.alertBlankTwoFactorAuthenticationCode()
            }
            else -> {
                launchUI(strategy) {
                    val client = factory.create(server)
                    view.showLoading()
                    try {
                        // The token is saved via the client TokenProvider
                        val token = retryIO("login") {
                            client.login(usernameOrEmail, password, twoFactorAuthenticationCode)
                        }
                        val me = retryIO("me") { client.me() }
                        saveAccount(me)
                        tokenRepository.save(server, token)
                        registerPushToken()
                        navigator.toChatList()
                    } catch (exception: RocketChatException) {
                        if (exception is RocketChatAuthException) {
                            view.alertInvalidTwoFactorAuthenticationCode()
                        } else {
                            exception.message?.let {
                                view.showMessage(it)
                            }.ifNull {
                                view.showGenericErrorMessage()
                            }
                        }
                    } finally {
                        view.hideLoading()
                    }
                }
            }
        }
    }

    fun signup() = navigator.toSignUp()

    private suspend fun registerPushToken() {
        localRepository.get(LocalRepository.KEY_PUSH_TOKEN)?.let {
            client.registerPushToken(it, getAccountsInteractor.get(), factory)
        }
        // TODO: When the push token is null, at some point we should receive it with
        // onTokenRefresh() on FirebaseTokenService, we need to confirm it.
    }

    private suspend fun saveAccount(me: Myself) {
        val icon = settings.favicon()?.let {
            currentServer.serverLogoUrl(it)
        }
        val logo = settings.wideTile()?.let {
            currentServer.serverLogoUrl(it)
        }
        val thumb = currentServer.avatarUrl(me.username!!)
        val account = Account(currentServer, icon, logo, me.username!!, thumb)
        saveAccountInteractor.save(account)
    }
}