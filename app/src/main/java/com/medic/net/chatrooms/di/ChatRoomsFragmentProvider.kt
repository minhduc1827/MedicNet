package com.medic.net.chatrooms.di

import com.medic.net.chatrooms.ui.ChatRoomsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ChatRoomsFragmentProvider {

    @ContributesAndroidInjector(modules = [ChatRoomsFragmentModule::class])
    abstract fun provideChatRoomsFragment(): ChatRoomsFragment
}