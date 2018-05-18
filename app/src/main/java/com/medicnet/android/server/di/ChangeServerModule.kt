package com.medicnet.android.server.di

import android.arch.lifecycle.LifecycleOwner
import com.medicnet.android.core.lifecycle.CancelStrategy
import com.medicnet.android.dagger.scope.PerActivity
import com.medicnet.android.server.presentation.ChangeServerNavigator
import com.medicnet.android.server.presentation.ChangeServerView
import com.medicnet.android.server.ui.ChangeServerActivity
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