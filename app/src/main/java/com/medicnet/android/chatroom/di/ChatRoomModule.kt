package com.medicnet.android.chatroom.di

import com.medicnet.android.chatroom.presentation.ChatRoomNavigator
import com.medicnet.android.chatroom.ui.ChatRoomActivity
import com.medicnet.android.dagger.scope.PerActivity
import dagger.Module
import dagger.Provides

@Module
@PerActivity
class ChatRoomModule {
    @Provides
    fun provideChatRoomNavigator(activity: ChatRoomActivity) = ChatRoomNavigator(activity)
}