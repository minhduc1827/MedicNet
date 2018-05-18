package com.medic.net.dagger

import android.app.Application
import com.medic.net.app.AppLifecycleObserver
import com.medic.net.app.RocketChatApplication
import com.medic.net.chatroom.service.MessageService
import com.medic.net.dagger.module.ActivityBuilder
import com.medic.net.dagger.module.AppModule
import com.medic.net.dagger.module.ReceiverBuilder
import com.medic.net.dagger.module.ServiceBuilder
import com.medic.net.push.FirebaseTokenService
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class,
    AppModule::class, ActivityBuilder::class, ServiceBuilder::class, ReceiverBuilder::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: RocketChatApplication)

    fun inject(service: FirebaseTokenService)

    fun inject(service: MessageService)

    /*@Component.Builder
    abstract class Builder : AndroidInjector.Builder<RocketChatApplication>()*/
}
