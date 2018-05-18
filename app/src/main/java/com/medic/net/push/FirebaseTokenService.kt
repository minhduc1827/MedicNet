package com.medic.net.push

import com.medic.net.R
import com.medic.net.infrastructure.LocalRepository
import com.medic.net.server.domain.GetCurrentServerInteractor
import com.medic.net.server.infraestructure.RocketChatClientFactory
import com.medic.net.util.retryIO
import chat.rocket.common.RocketChatException
import chat.rocket.core.internal.rest.registerPushToken
import com.google.android.gms.gcm.GoogleCloudMessaging
import com.google.android.gms.iid.InstanceID
import com.google.firebase.iid.FirebaseInstanceIdService
import dagger.android.AndroidInjection
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import javax.inject.Inject

class FirebaseTokenService : FirebaseInstanceIdService() {

    @Inject
    lateinit var factory: RocketChatClientFactory
    @Inject
    lateinit var getCurrentServerInteractor: GetCurrentServerInteractor

    @Inject
    lateinit var localRepository: LocalRepository

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

    override fun onTokenRefresh() {
        //TODO: We need to use the Cordova Project gcm_sender_id since it's the one configured on RC
        // default push gateway. We should register this project's own project sender id into it.
        try {
            val gcmToken = InstanceID.getInstance(this)
                    .getToken(getString(R.string.gcm_sender_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null)
            val currentServer = getCurrentServerInteractor.get()
            val client = currentServer?.let { factory.create(currentServer) }

            gcmToken?.let {
                localRepository.save(LocalRepository.KEY_PUSH_TOKEN, gcmToken)
                client?.let {
                    launch {
                        try {
                            Timber.d("Registering push token: $gcmToken for ${client.url}")
                            retryIO("register push token") { client.registerPushToken(gcmToken)  }
                        } catch (ex: RocketChatException) {
                            Timber.e(ex, "Error registering push token")
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Timber.d(ex, "Error refreshing Firebase TOKEN")
        }
    }
}