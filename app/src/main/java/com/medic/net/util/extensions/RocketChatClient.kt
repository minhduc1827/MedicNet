package com.medic.net.util.extensions

import com.medic.net.server.domain.model.Account
import com.medic.net.server.infraestructure.RocketChatClientFactory
import com.medic.net.util.retryIO
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.rest.registerPushToken
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