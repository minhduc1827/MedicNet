package com.medic.net.members.di

import android.arch.lifecycle.LifecycleOwner
import com.medic.net.chatroom.ui.ChatRoomActivity
import com.medic.net.core.lifecycle.CancelStrategy
import com.medic.net.dagger.scope.PerFragment
import com.medic.net.members.presentation.MembersNavigator
import com.medic.net.members.presentation.MembersView
import com.medic.net.members.ui.MembersFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
@PerFragment
class MembersFragmentModule {

    @Provides
    fun provideChatRoomNavigator(activity: ChatRoomActivity) = MembersNavigator(activity)

    @Provides
    fun membersView(frag: MembersFragment): MembersView {
        return frag
    }

    @Provides
    fun provideLifecycleOwner(frag: MembersFragment): LifecycleOwner {
        return frag
    }

    @Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}