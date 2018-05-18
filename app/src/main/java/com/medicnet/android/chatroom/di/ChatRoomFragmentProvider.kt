package com.medicnet.android.chatroom.di

import com.medicnet.android.chatroom.ui.ChatRoomFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ChatRoomFragmentProvider {

    @ContributesAndroidInjector(modules = [ChatRoomFragmentModule::class])
    abstract fun provideChatRoomFragment(): ChatRoomFragment
}