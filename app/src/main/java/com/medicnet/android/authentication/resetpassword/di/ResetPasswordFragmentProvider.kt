package com.medicnet.android.authentication.resetpassword.di

import com.medicnet.android.authentication.resetpassword.ui.ResetPasswordFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ResetPasswordFragmentProvider {

    @ContributesAndroidInjector(modules = [ResetPasswordFragmentModule::class])
    abstract fun provideResetPasswordFragment(): ResetPasswordFragment
}