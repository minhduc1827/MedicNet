package com.medic.net.push.di

import com.medic.net.dagger.module.AppModule
import com.medic.net.push.DeleteReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DeleteReceiverProvider {
    @ContributesAndroidInjector(modules = [AppModule::class])
    abstract fun provideDeleteReceiver(): DeleteReceiver
}