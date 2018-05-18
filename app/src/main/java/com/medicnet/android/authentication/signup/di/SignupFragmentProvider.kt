package com.medicnet.android.authentication.signup.di

import com.medicnet.android.authentication.signup.ui.SignupFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SignupFragmentProvider {

    @ContributesAndroidInjector(modules = [SignupFragmentModule::class])
    abstract fun provideSignupFragment(): SignupFragment
}