package com.medicnet.android.chatroom.di

import android.arch.lifecycle.LifecycleOwner
import com.medicnet.android.dagger.scope.PerFragment
import com.medicnet.android.favoritemessages.presentation.FavoriteMessagesView
import com.medicnet.android.favoritemessages.ui.FavoriteMessagesFragment
import dagger.Module
import dagger.Provides

@Module
@PerFragment
class FavoriteMessagesFragmentModule {

    @Provides
    fun provideLifecycleOwner(frag: FavoriteMessagesFragment): LifecycleOwner {
        return frag
    }

    /*@Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }*/

    @Provides
    fun provideFavoriteMessagesView(frag: FavoriteMessagesFragment): FavoriteMessagesView {
        return frag
    }
}