package com.medicnet.android.authentication.registerusername.di

import com.medicnet.android.authentication.registerusername.ui.RegisterUsernameFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class RegisterUsernameFragmentProvider {

    @ContributesAndroidInjector(modules = [RegisterUsernameFragmentModule::class])
    abstract fun provideRegisterUsernameFragment(): RegisterUsernameFragment
}