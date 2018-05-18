package com.medicnet.android.profile.di

import com.medicnet.android.profile.ui.ProfileFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ProfileFragmentProvider {

    @ContributesAndroidInjector(modules = [ProfileFragmentModule::class])
    abstract fun provideProfileFragment(): ProfileFragment
}