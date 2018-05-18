package com.medic.net.authentication.twofactor.di

import com.medic.net.authentication.twofactor.ui.TwoFAFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module abstract class TwoFAFragmentProvider {

    @ContributesAndroidInjector(modules = [TwoFAFragmentModule::class])
    abstract fun provideTwoFAFragment(): TwoFAFragment
}
