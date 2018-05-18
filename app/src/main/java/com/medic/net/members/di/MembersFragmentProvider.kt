package com.medic.net.members.di

import com.medic.net.members.ui.MembersFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MembersFragmentProvider {

    @ContributesAndroidInjector(modules = [MembersFragmentModule::class])
    abstract fun provideMembersFragment(): MembersFragment
}