package com.medic.net.chatroom.di

import android.arch.lifecycle.LifecycleOwner
import com.medic.net.core.lifecycle.CancelStrategy
import com.medic.net.dagger.scope.PerFragment
import com.medic.net.pinnedmessages.presentation.PinnedMessagesView
import com.medic.net.pinnedmessages.ui.PinnedMessagesFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
@PerFragment
class PinnedMessagesFragmentModule {

    @Provides
    fun provideLifecycleOwner(frag: PinnedMessagesFragment): LifecycleOwner {
        return frag
    }

    @Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }

    @Provides
    fun providePinnedMessagesView(frag: PinnedMessagesFragment): PinnedMessagesView {
        return frag
    }
}