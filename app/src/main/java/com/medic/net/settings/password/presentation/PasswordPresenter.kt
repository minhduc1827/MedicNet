package com.medic.net.settings.password.presentation

import com.medic.net.core.lifecycle.CancelStrategy
import com.medic.net.server.domain.GetCurrentServerInteractor
import com.medic.net.server.infraestructure.RocketChatClientFactory
import com.medic.net.util.extensions.launchUI
import com.medic.net.util.retryIO
import chat.rocket.common.RocketChatException
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.rest.me
import chat.rocket.core.internal.rest.updateProfile
import javax.inject.Inject

class PasswordPresenter @Inject constructor(
    private val view: PasswordView,
    private val strategy: CancelStrategy,
    serverInteractor: GetCurrentServerInteractor,
    factory: RocketChatClientFactory
) {
    private val serverUrl = serverInteractor.get()!!
    private val client: RocketChatClient = factory.create(serverUrl)

    fun updatePassword(password: String) {
        launchUI(strategy) {
            try {
                view.showLoading()

                val me = retryIO("me") { client.me() }
                retryIO("updateProfile(${me.id})") {
                    client.updateProfile(me.id!!, null, null, password, null)
                }

                view.showPasswordSuccessfullyUpdatedMessage()
                view.hideLoading()
            } catch (exception: RocketChatException) {
                view.showPasswordFailsUpdateMessage(exception.message)
                view.hideLoading()
            }
        }
    }
}