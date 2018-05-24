package com.medicnet.android.authentication.selectorganization.di

import com.medicnet.android.authentication.selectorganization.ui.SelectOrganizationFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author duc.nguyen
 * @since 5/24/2018
 */
@Module
abstract class SelectOrganizationProvider {

    @ContributesAndroidInjector(modules = [SelectOrganizationModule::class])
    abstract fun provideLoginFragment(): SelectOrganizationFragment
}