package com.medicnet.android.main.di

import android.arch.lifecycle.LifecycleOwner
import com.medicnet.android.core.lifecycle.CancelStrategy
import com.medicnet.android.dagger.scope.PerActivity
import com.medicnet.android.main.presentation.MainNavigator
import com.medicnet.android.main.presentation.MainView
import com.medicnet.android.main.ui.MainActivity
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
class MainModule {

    @Provides
    @PerActivity
    fun provideMainNavigator(activity: MainActivity) = MainNavigator(activity)

    @Provides
    fun provideMainView(activity: MainActivity): MainView = activity

    @Provides
    fun provideLifecycleOwner(activity: MainActivity): LifecycleOwner = activity

    @Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy = CancelStrategy(owner, jobs)
}