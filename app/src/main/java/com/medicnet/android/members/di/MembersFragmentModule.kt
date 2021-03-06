package com.medicnet.android.members.di

import android.arch.lifecycle.LifecycleOwner
import com.medicnet.android.dagger.scope.PerFragment
import com.medicnet.android.main.ui.MainActivity
import com.medicnet.android.members.presentation.MembersNavigator
import com.medicnet.android.members.presentation.MembersView
import com.medicnet.android.members.ui.MembersFragment
import dagger.Module
import dagger.Provides

@Module
@PerFragment
class MembersFragmentModule {

    @Provides
    fun provideChatRoomNavigator(activity: MainActivity) = MembersNavigator(activity)

    @Provides
    fun membersView(frag: MembersFragment): MembersView {
        return frag
    }

    @Provides
    fun provideLifecycleOwner(frag: MembersFragment): LifecycleOwner {
        return frag
    }

    /* @Provides
     fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
         return CancelStrategy(owner, jobs)
     }*/
}