package com.medic.net.profile.di

import com.medic.net.profile.ui.ProfileFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ProfileFragmentProvider {

    @ContributesAndroidInjector(modules = [ProfileFragmentModule::class])
    abstract fun provideProfileFragment(): ProfileFragment
}