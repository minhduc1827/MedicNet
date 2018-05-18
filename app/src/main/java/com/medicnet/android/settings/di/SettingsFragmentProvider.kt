package com.medicnet.android.settings.di

import com.medicnet.android.settings.ui.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SettingsFragmentProvider {
    @ContributesAndroidInjector(modules = [SettingsFragmentModule::class])
    abstract fun provideSettingsFragment(): SettingsFragment
}