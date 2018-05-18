package com.medic.net.chatroom.di

import com.medic.net.chatroom.ui.ChatRoomFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ChatRoomFragmentProvider {

    @ContributesAndroidInjector(modules = [ChatRoomFragmentModule::class])
    abstract fun provideChatRoomFragment(): ChatRoomFragment
}