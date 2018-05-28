package com.medicnet.android.app

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import chat.rocket.common.RocketChatException
import chat.rocket.common.model.UserStatus
import chat.rocket.core.internal.realtime.setTemporaryStatus
import com.medicnet.android.server.domain.GetAccountInteractor
import com.medicnet.android.server.domain.GetCurrentServerInteractor
import com.medicnet.android.server.infraestructure.RocketChatClientFactory
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import javax.inject.Inject

class AppLifecycleObserver @Inject constructor(
    private val serverInteractor: GetCurrentServerInteractor,
    private val factory: RocketChatClientFactory,
    private val getAccountInteractor: GetAccountInteractor
) : LifecycleObserver {

    var callback: ((isForeground: Boolean) -> Unit)? = null

    fun setOnLifeCycleCallBack(callback: (authenticated: Boolean) -> Unit) {
        this.callback = callback
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnterForeground() {
        changeTemporaryStatus(UserStatus.Online())
        callback?.invoke(true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnterBackground() {
        changeTemporaryStatus(UserStatus.Away())
        callback?.invoke(false)
    }

    private fun changeTemporaryStatus(userStatus: UserStatus) {
        launch {
            val currentServer = serverInteractor.get()
            val account = currentServer?.let { getAccountInteractor.get(currentServer) }
            val client = account?.let { factory.create(currentServer) }

            try {
                client?.setTemporaryStatus(userStatus)
            } catch (exception: RocketChatException) {
                Timber.e(exception)
            }
        }
    }
}