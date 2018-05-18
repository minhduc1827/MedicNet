package com.medic.net.authentication.signup.di

import com.medic.net.authentication.signup.ui.SignupFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SignupFragmentProvider {

    @ContributesAndroidInjector(modules = [SignupFragmentModule::class])
    abstract fun provideSignupFragment(): SignupFragment
}