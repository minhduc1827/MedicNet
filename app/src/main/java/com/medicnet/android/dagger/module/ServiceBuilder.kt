package com.medicnet.android.dagger.module

import com.medicnet.android.chatroom.di.MessageServiceProvider
import com.medicnet.android.chatroom.service.MessageService
import com.medicnet.android.push.FirebaseTokenService
import com.medicnet.android.push.GcmListenerService
import com.medicnet.android.push.di.FirebaseTokenServiceProvider
import com.medicnet.android.push.di.GcmListenerServiceProvider
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