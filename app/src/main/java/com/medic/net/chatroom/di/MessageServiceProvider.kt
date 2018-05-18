package com.medic.net.chatroom.di

import com.medic.net.chatroom.service.MessageService
import com.medic.net.dagger.module.AppModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module abstract class MessageServiceProvider {
    @ContributesAndroidInjector(modules = [AppModule::class])
    abstract fun provideMessageService(): MessageService
}