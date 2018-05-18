package com.medic.net.dagger.module

import com.medic.net.chatroom.di.MessageServiceProvider
import com.medic.net.chatroom.service.MessageService
import com.medic.net.push.FirebaseTokenService
import com.medic.net.push.GcmListenerService
import com.medic.net.push.di.FirebaseTokenServiceProvider
import com.medic.net.push.di.GcmListenerServiceProvider
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module abstract class ServiceBuilder {

    @ContributesAndroidInjector(modules = [FirebaseTokenServiceProvider::class])
    abstract fun bindFirebaseTokenService(): FirebaseTokenService

    @ContributesAndroidInjector(modules = [GcmListenerServiceProvider::class])
    abstract fun bindGcmListenerService(): GcmListenerService

    @ContributesAndroidInjector(modules = [MessageServiceProvider::class])
    abstract fun bindMessageService(): MessageService
}