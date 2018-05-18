package com.medic.net.server.di

import android.arch.lifecycle.LifecycleOwner
import com.medic.net.core.lifecycle.CancelStrategy
import com.medic.net.dagger.scope.PerActivity
import com.medic.net.server.presentation.ChangeServerNavigator
import com.medic.net.server.presentation.ChangeServerView
import com.medic.net.server.ui.ChangeServerActivity
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
class ChangeServerModule {
    @Provides
    @PerActivity
    fun provideChangeServerNavigator(activity: ChangeServerActivity) = ChangeServerNavigator(activity)

    @Provides
    @PerActivity
    fun ChangeServerView(activity: ChangeServerActivity): ChangeServerView {
        return activity
    }

    @Provides
    fun provideLifecycleOwner(activity: ChangeServerActivity): LifecycleOwner = activity

    @Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy = CancelStrategy(owner, jobs)
}