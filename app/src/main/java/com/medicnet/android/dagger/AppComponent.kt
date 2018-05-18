package com.medicnet.android.dagger

import android.app.Application
import com.medicnet.android.app.RocketChatApplication
import com.medicnet.android.chatroom.service.MessageService
import com.medicnet.android.dagger.module.ActivityBuilder
import com.medicnet.android.dagger.module.AppModule
import com.medicnet.android.dagger.module.ReceiverBuilder
import com.medicnet.android.dagger.module.ServiceBuilder
import com.medicnet.android.push.FirebaseTokenService
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
