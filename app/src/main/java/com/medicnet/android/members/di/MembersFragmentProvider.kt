package com.medicnet.android.members.di

import com.medicnet.android.members.ui.MembersFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MembersFragmentProvider {

    @ContributesAndroidInjector(modules = [MembersFragmentModule::class])
    abstract fun provideMembersFragment(): MembersFragment
}