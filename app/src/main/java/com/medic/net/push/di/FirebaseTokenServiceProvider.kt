package com.medic.net.push.di

import com.medic.net.dagger.module.AppModule
import com.medic.net.push.FirebaseTokenService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module abstract class FirebaseTokenServiceProvider {
    @ContributesAndroidInjector(modules = [AppModule::class])
    abstract fun provideFirebaseTokenService(): FirebaseTokenService
}