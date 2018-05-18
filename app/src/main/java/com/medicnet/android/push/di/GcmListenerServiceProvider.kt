package com.medicnet.android.push.di

import com.medicnet.android.dagger.module.AppModule
import com.medicnet.android.push.GcmListenerService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module abstract class GcmListenerServiceProvider {
    @ContributesAndroidInjector(modules = [AppModule::class])
    abstract fun provideGcmListenerService(): GcmListenerService
}