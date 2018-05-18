package com.medic.net.chatrooms.di

import android.arch.lifecycle.LifecycleOwner
import com.medic.net.chatrooms.presentation.ChatRoomsView
import com.medic.net.chatrooms.ui.ChatRoomsFragment
import com.medic.net.dagger.scope.PerFragment
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