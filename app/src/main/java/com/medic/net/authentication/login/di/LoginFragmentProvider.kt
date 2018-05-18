package com.medic.net.authentication.login.di

import com.medic.net.authentication.login.ui.LoginFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module abstract class LoginFragmentProvider {

    @ContributesAndroidInjector(modules = [LoginFragmentModule::class])
    abstract fun provideLoginFragment(): LoginFragment
}