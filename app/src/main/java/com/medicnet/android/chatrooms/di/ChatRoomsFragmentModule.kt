package com.medicnet.android.chatrooms.di

import android.arch.lifecycle.LifecycleOwner
import com.medicnet.android.chatrooms.presentation.ChatRoomsView
import com.medicnet.android.chatrooms.ui.ChatRoomsFragment
import com.medicnet.android.dagger.scope.PerFragment
import dagger.Module
import dagger.Provides

@Module
@PerFragment
class ChatRoomsFragmentModule {

    @Provides
    fun chatRoomsView(frag: ChatRoomsFragment): ChatRoomsView {
        return frag
    }

    @Provides
    fun provideLifecycleOwner(frag: ChatRoomsFragment): LifecycleOwner {
        return frag
    }
}