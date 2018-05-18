package com.medicnet.android.chatrooms.di

import com.medicnet.android.chatrooms.ui.ChatRoomsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ChatRoomsFragmentProvider {

    @ContributesAndroidInjector(modules = [ChatRoomsFragmentModule::class])
    abstract fun provideChatRoomsFragment(): ChatRoomsFragment
}