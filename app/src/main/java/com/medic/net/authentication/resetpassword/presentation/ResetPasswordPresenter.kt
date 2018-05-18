package com.medic.net.authentication.resetpassword.presentation

import com.medic.net.authentication.presentation.AuthenticationNavigator
import com.medic.net.core.lifecycle.CancelStrategy
import com.medic.net.server.domain.GetCurrentServerInteractor
import com.medic.net.server.infraestructure.RocketChatClientFactory
import com.medic.net.util.extensions.isEmail
import com.medic.net.util.extensions.launchUI
import com.medic.net.util.retryIO
import chat.rocket.common.RocketChatException
import chat.rocket.common.RocketChatInvalidResponseException
import chat.rocket.common.util.ifNull
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.rest.forgotPassword
import javax.inject.Inject

class ResetPasswordPresenter @Inject constructor(
    private val view: ResetPasswordView,
    private val strategy: CancelStrategy,
    private val navigator: AuthenticationNavigator,
    factory: RocketChatClientFactory,
    serverInteractor: GetCurrentServerInteractor
) {
    private val currentServer = serverInteractor.get()!!
    private val client: RocketChatClient = factory.create(currentServer)

    fun resetPassword(email: String) {
        when {
            email.isBlank() -> view.alertBlankEmail()
            !email.isEmail() -> view.alertInvalidEmail()
            else -> launchUI(strategy) {
                view.showLoading()
                try {
                    retryIO("forgotPassword(email = $email)") {
                        client.forgotPassword(email)
                    }
                    navigator.toPreviousView()
                    view.emailSent()
                } catch (exception: RocketChatException) {
                    if (exception is RocketChatInvalidResponseException) {
                        view.updateYourServerVersion()
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