package com.medic.net.chatroom.di

import com.medic.net.pinnedmessages.ui.PinnedMessagesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PinnedMessagesFragmentProvider {

    @ContributesAndroidInjector(modules = [PinnedMessagesFragmentModule::class])
    abstract fun providePinnedMessageFragment(): PinnedMessagesFragment
}