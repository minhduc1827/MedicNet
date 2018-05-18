package com.medicnet.android.profile.presentation

import chat.rocket.common.RocketChatException
import chat.rocket.common.util.ifNull
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.rest.me
import chat.rocket.core.internal.rest.setAvatar
import chat.rocket.core.internal.rest.updateProfile
import com.medicnet.android.core.behaviours.showMessage
import com.medicnet.android.core.lifecycle.CancelStrategy
import com.medicnet.android.server.domain.GetCurrentServerInteractor
import com.medicnet.android.server.infraestructure.RocketChatClientFactory
import com.medicnet.android.util.extensions.avatarUrl
import com.medicnet.android.util.extensions.launchUI
import com.medicnet.android.util.retryIO
import javax.inject.Inject

class ProfilePresenter @Inject constructor(private val view: ProfileView,
                                           private val strategy: CancelStrategy,
                                           serverInteractor: GetCurrentServerInteractor,
                                           factory: RocketChatClientFactory) {
    private val serverUrl = serverInteractor.get()!!
    private val client: RocketChatClient = factory.create(serverUrl)
    private lateinit var myselfId: String

    fun loadUserProfile() {
        launchUI(strategy) {
            view.showLoading()
            try {
                val myself = retryIO("me") { client.me() }
                val id = myself.id
                val username = myself.username
                if (id == null || username == null) {
                    view.showGenericErrorMessage()
                } else {
                    myselfId = id
                    val avatarUrl = serverUrl.avatarUrl(username)
                    val email = myself.emails?.getOrNull(0)?.address
                    view.showProfile(
                            avatarUrl,
                            myself.name ?: "",
                            myself.username ?: "",
                            email
                    )
                }
            } catch (exception: RocketChatException) {
                view.showMessage(exception)
            } finally {
                view.hideLoading()
            }
        }
    }

    fun updateUserProfile(email: String, name: String, username: String, avatarUrl: String = "") {
        launchUI(strategy) {
            view.showLoading()
            try {
                if(avatarUrl!="") {
                    retryIO { client.setAvatar(avatarUrl) }
                }
                val user = retryIO { client.updateProfile(myselfId, email, name, username) }
                view.showProfileUpdateSuccessfullyMessage()
                loadUserProfile()
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