package com.medicnet.android.util.extensions

import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.rest.registerPushToken
import com.medicnet.android.server.domain.model.Account
import com.medicnet.android.server.infraestructure.RocketChatClientFactory
import com.medicnet.android.util.retryIO
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import timber.log.Timber

suspend fun RocketChatClient.registerPushToken(
    token: String,
    accounts: List<Account>,
    factory: RocketChatClientFactory
) {
    launch(CommonPool) {
        accounts.forEach { account ->
            try {
                retryIO(description = "register push token: ${account.serverUrl}") {
                    factory.create(account.serverUrl).registerPushToken(token)
                }
            } catch (ex: Exception) {
                Timber.d(ex, "Error registering Push token for ${account.serverUrl}")
                ex.printStackTrace()
            }
        }
    }
}