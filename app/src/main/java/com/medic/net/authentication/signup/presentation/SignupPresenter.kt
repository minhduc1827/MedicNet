package com.medic.net.authentication.signup.presentation

import com.medic.net.authentication.presentation.AuthenticationNavigator
import com.medic.net.core.lifecycle.CancelStrategy
import com.medic.net.infrastructure.LocalRepository
import com.medic.net.server.domain.*
import com.medic.net.server.domain.model.Account
import com.medic.net.server.infraestructure.RocketChatClientFactory
import com.medic.net.util.extensions.avatarUrl
import com.medic.net.util.extensions.launchUI
import com.medic.net.util.extensions.privacyPolicyUrl
import com.medic.net.util.extensions.registerPushToken
import com.medic.net.util.extensions.serverLogoUrl
import com.medic.net.util.extensions.termsOfServiceUrl
import com.medic.net.util.retryIO
import chat.rocket.common.RocketChatException
import chat.rocket.common.util.ifNull
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.rest.login
import chat.rocket.core.internal.rest.me
import chat.rocket.core.internal.rest.signup
import chat.rocket.core.model.Myself
import javax.inject.Inject

class SignupPresenter @Inject constructor(private val view: SignupView,
                                          private val strategy: CancelStrategy,
                                          private val navigator: AuthenticationNavigator,
                                          private val localRepository: LocalRepository,
                                          private val serverInteractor: GetCurrentServerInteractor,
                                          private val factory: RocketChatClientFactory,
                                          private val saveAccountInteractor: SaveAccountInteractor,
                                          private val getAccountsInteractor: GetAccountsInteractor,
                                          settingsInteractor: GetSettingsInteractor) {
    private val currentServer = serverInteractor.get()!!
    private val client: RocketChatClient = factory.create(currentServer)
    private var settings: PublicSettings = settingsInteractor.get(serverInteractor.get()!!)

    fun signup(name: String, username: String, password: String, email: String) {
        val server = serverInteractor.get()
        when {
            server == null -> {
                navigator.toServerScreen()
            }
            name.isBlank() -> {
                view.alertBlankName()
            }
            username.isBlank() -> {
                view.alertBlankUsername()
            }
            password.isEmpty() -> {
                view.alertEmptyPassword()
            }
            email.isBlank() -> {
                view.alertBlankEmail()
            }
            else -> {
                val client = factory.create(server)
                launchUI(strategy) {
                    view.showLoading()
                    try {
                        // TODO This function returns a user so should we save it?
                        retryIO("signup") { client.signup(email, name, username, password) }
                        // TODO This function returns a user token so should we save it?
                        retryIO("login") { client.login(username, password) }
                        val me = retryIO("me") { client.me() }
                        localRepository.save(LocalRepository.CURRENT_USERNAME_KEY, me.username)
                        saveAccount(me)
                        registerPushToken()
                        navigator.toChatList()
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
    }

    fun termsOfService() {
        serverInteractor.get()?.let {
            navigator.toWebPage(it.termsOfServiceUrl())
        }
    }

    fun privacyPolicy() {
        serverInteractor.get()?.let {
            navigator.toWebPage(it.privacyPolicyUrl())
        }
    }

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