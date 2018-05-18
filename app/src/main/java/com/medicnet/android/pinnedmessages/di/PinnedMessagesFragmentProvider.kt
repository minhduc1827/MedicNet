package com.medicnet.android.chatroom.di

import com.medicnet.android.pinnedmessages.ui.PinnedMessagesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PinnedMessagesFragmentProvider {

    @ContributesAndroidInjector(modules = [PinnedMessagesFragmentModule::class])
    abstract fun providePinnedMessageFragment(): PinnedMessagesFragment
}