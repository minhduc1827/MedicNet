package com.medicnet.android.push

import com.medicnet.android.dagger.module.AppModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DirectReplyReceiverProvider {
    @ContributesAndroidInjector(modules = [AppModule::class])
    abstract fun provideDirectReplyReceiver(): DirectReplyReceiver
}