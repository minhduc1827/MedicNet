package com.medic.net.settings.password.di

import com.medic.net.settings.password.ui.PasswordFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PasswordFragmentProvider {
    @ContributesAndroidInjector(modules = [PasswordFragmentModule::class])
    abstract fun providePasswordFragment(): PasswordFragment
}