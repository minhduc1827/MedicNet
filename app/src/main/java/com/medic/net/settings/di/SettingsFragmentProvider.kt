package com.medic.net.settings.di

import com.medic.net.settings.ui.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SettingsFragmentProvider {
    @ContributesAndroidInjector(modules = [SettingsFragmentModule::class])
    abstract fun provideSettingsFragment(): SettingsFragment
}