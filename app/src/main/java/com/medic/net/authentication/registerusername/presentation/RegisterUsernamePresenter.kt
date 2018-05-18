package com.medic.net.authentication.registerusername.presentation

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
import chat.rocket.common.RocketChatException
import chat.rocket.common.model.Token
import chat.rocket.common.util.ifNull
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.rest.updateOwnBasicInformation
import javax.inject.Inject

class RegisterUsernamePresenter @Inject constructor(
    private val view: RegisterUsernameView,
    private val strategy: CancelStrategy,
    private val navigator: AuthenticationNavigator,
    private val tokenRepository: TokenRepository,
    private val localRepository: LocalRepository,
    private val factory: RocketChatClientFactory,
    private val saveAccountInteractor: SaveAccountInteractor,
    private val getAccountsInteractor: GetAccountsInteractor,
    serverInteractor: GetCurrentServerInteractor,
    settingsInteractor: GetSettingsInteractor
) {
    private val currentServer = serverInteractor.get()!!
    private val client: RocketChatClient = factory.create(currentServer)
    private var settings: PublicSettings = settingsInteractor.get(serverInteractor.get()!!)

    fun registerUsername(username: String, userId: String, authToken: String) {
        if (username.isBlank()) {
            view.alertBlankUsername()
        } else {
            launchUI(strategy) {
                view.showLoading()
                try {
                    val me = retryIO("updateOwnBasicInformation(username = $username)") {
                        client.updateOwnBasicInformation(username = username)
                    }
                    val registeredUsername = me.username
                    if (registeredUsername != null) {
                        saveAccount(registeredUsername)
                        tokenRepository.save(currentServer, Token(userId, authToken))
                        registerPushToken()
                        navigator.toChatList()
                    }
                } catch (exception: RocketChatException) {
                    exception.message?.let {
                        view.showMessage(it)
                    }.ifNull {
                        view.showGenericErrorMessage()
                    }
                } finally {
                    view.hideLoading()
                }
            }
        }
    }

    private suspend fun registerPushToken() {
        localRepository.get(LocalRepository.KEY_PUSH_TOKEN)?.let {
            client.registerPushToken(it, getAccountsInteractor.get(), factory)
        }
        // TODO: When the push token is null, at some point we should receive it with
        // onTokenRefresh() on FirebaseTokenService, we need to confirm it.
    }

    private suspend fun saveAccount(username: String) {
        val icon = settings.favicon()?.let {
            currentServer.serverLogoUrl(it)
        }
        val logo = settings.wideTile()?.let {
            currentServer.serverLogoUrl(it)
        }
        val thumb = currentServer.avatarUrl(username)
        val account = Account(currentServer, icon, logo, username, thumb)
        saveAccountInteractor.save(account)
    }
}