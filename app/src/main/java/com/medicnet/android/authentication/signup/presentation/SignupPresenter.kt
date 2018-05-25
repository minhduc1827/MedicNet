package com.medicnet.android.authentication.signup.presentation

import chat.rocket.common.RocketChatException
import chat.rocket.common.util.ifNull
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.rest.login
import chat.rocket.core.internal.rest.me
import chat.rocket.core.internal.rest.signup
import chat.rocket.core.model.Myself
import com.medicnet.android.authentication.presentation.AuthenticationNavigator
import com.medicnet.android.core.lifecycle.CancelStrategy
import com.medicnet.android.infrastructure.LocalRepository
import com.medicnet.android.server.domain.*
import com.medicnet.android.server.domain.model.Account
import com.medicnet.android.server.infraestructure.RocketChatClientFactory
import com.medicnet.android.util.extensions.*
import com.medicnet.android.util.retryIO
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
    private val selectRole: String = "Select Role"
    private val selectOrganization: String = "Select Organisation"

    fun signup(name: String, username: String, role: String, organization: String, password: String, email: String) {
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
            role.isBlank() || role.equals(selectRole) -> {
                view.alertEmptyRole()
            }
            organization.isBlank() || organization.equals(selectOrganization) -> {
                view.alertEmptyOrganization()
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
                        retryIO("signup") { client.signup(email, name, role, organization, username, password) }
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

    fun toSelectOrganization(json: String) {
        navigator.toSelectOrganization(json)
    }

    fun toSelectRole(json: String) {
        navigator.toSelectRole(json)
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