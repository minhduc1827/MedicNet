package com.medic.net.chatroom.di

import android.arch.lifecycle.LifecycleOwner
import com.medic.net.core.lifecycle.CancelStrategy
import com.medic.net.dagger.scope.PerFragment
import com.medic.net.favoritemessages.presentation.FavoriteMessagesView
import com.medic.net.favoritemessages.ui.FavoriteMessagesFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
@PerFragment
class FavoriteMessagesFragmentModule {

    @Provides
    fun provideLifecycleOwner(frag: FavoriteMessagesFragment): LifecycleOwner {
        return frag
    }

    @Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }

    @Provides
    fun provideFavoriteMessagesView(frag: FavoriteMessagesFragment): FavoriteMessagesView {
        return frag
    }
}