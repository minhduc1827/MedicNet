package com.medicnet.android.chatroom.di

import com.medicnet.android.chatroom.service.MessageService
import com.medicnet.android.dagger.module.AppModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module abstract class MessageServiceProvider {
    @ContributesAndroidInjector(modules = [AppModule::class])
    abstract fun provideMessageService(): MessageService
}