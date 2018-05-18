package com.medicnet.android.authentication.twofactor.di

import com.medicnet.android.authentication.twofactor.ui.TwoFAFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module abstract class TwoFAFragmentProvider {

    @ContributesAndroidInjector(modules = [TwoFAFragmentModule::class])
    abstract fun provideTwoFAFragment(): TwoFAFragment
}
