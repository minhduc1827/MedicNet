package com.medic.net.push

import com.medic.net.dagger.module.AppModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DirectReplyReceiverProvider {
    @ContributesAndroidInjector(modules = [AppModule::class])
    abstract fun provideDirectReplyReceiver(): DirectReplyReceiver
}