package com.medic.net.chatroom.di

import android.arch.lifecycle.LifecycleOwner
import com.medic.net.chatroom.presentation.ChatRoomNavigator
import com.medic.net.chatroom.presentation.ChatRoomView
import com.medic.net.chatroom.ui.ChatRoomActivity
import com.medic.net.chatroom.ui.ChatRoomFragment
import com.medic.net.core.lifecycle.CancelStrategy
import com.medic.net.dagger.scope.PerFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
@PerFragment
class ChatRoomFragmentModule {

    @Provides
    fun chatRoomView(frag: ChatRoomFragment): ChatRoomView {
        return frag
    }

    @Provides
    fun provideLifecycleOwner(frag: ChatRoomFragment): LifecycleOwner {
        return frag
    }

    @Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}