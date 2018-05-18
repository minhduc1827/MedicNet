package com.medicnet.android.push.di

import com.medicnet.android.dagger.module.AppModule
import com.medicnet.android.push.DeleteReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DeleteReceiverProvider {
    @ContributesAndroidInjector(modules = [AppModule::class])
    abstract fun provideDeleteReceiver(): DeleteReceiver
}