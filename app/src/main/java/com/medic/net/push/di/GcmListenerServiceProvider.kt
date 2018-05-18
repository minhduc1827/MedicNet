package com.medic.net.push.di

import com.medic.net.dagger.module.AppModule
import com.medic.net.push.GcmListenerService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module abstract class GcmListenerServiceProvider {
    @ContributesAndroidInjector(modules = [AppModule::class])
    abstract fun provideGcmListenerService(): GcmListenerService
}