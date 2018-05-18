package com.medic.net.chatroom.di

import com.medic.net.chatroom.presentation.ChatRoomNavigator
import com.medic.net.chatroom.ui.ChatRoomActivity
import com.medic.net.dagger.scope.PerActivity
import dagger.Module
import dagger.Provides

@Module
@PerActivity
class ChatRoomModule {
    @Provides
    fun provideChatRoomNavigator(activity: ChatRoomActivity) = ChatRoomNavigator(activity)
}