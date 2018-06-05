package com.medicnet.android.chatroom.di

import android.arch.lifecycle.LifecycleOwner
import com.medicnet.android.dagger.scope.PerFragment
import com.medicnet.android.pinnedmessages.presentation.PinnedMessagesView
import com.medicnet.android.pinnedmessages.ui.PinnedMessagesFragment
import dagger.Module
import dagger.Provides

@Module
@PerFragment
class PinnedMessagesFragmentModule {

    @Provides
    fun provideLifecycleOwner(frag: PinnedMessagesFragment): LifecycleOwner {
        return frag
    }

    /*@Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }*/

    @Provides
    fun providePinnedMessagesView(frag: PinnedMessagesFragment): PinnedMessagesView {
        return frag
    }
}