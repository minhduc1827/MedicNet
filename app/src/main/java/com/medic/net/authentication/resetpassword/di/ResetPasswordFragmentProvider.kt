package com.medic.net.authentication.resetpassword.di

import com.medic.net.authentication.resetpassword.ui.ResetPasswordFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ResetPasswordFragmentProvider {

    @ContributesAndroidInjector(modules = [ResetPasswordFragmentModule::class])
    abstract fun provideResetPasswordFragment(): ResetPasswordFragment
}