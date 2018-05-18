package com.medicnet.android.push.di

import com.medicnet.android.dagger.module.AppModule
import com.medicnet.android.push.FirebaseTokenService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module abstract class FirebaseTokenServiceProvider {
    @ContributesAndroidInjector(modules = [AppModule::class])
    abstract fun provideFirebaseTokenService(): FirebaseTokenService
}