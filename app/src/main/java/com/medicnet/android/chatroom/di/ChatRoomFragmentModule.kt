package com.medicnet.android.chatroom.di

import android.arch.lifecycle.LifecycleOwner
import com.medicnet.android.chatroom.presentation.ChatRoomView
import com.medicnet.android.chatroom.ui.ChatRoomFragment
import com.medicnet.android.core.lifecycle.CancelStrategy
import com.medicnet.android.dagger.scope.PerFragment
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