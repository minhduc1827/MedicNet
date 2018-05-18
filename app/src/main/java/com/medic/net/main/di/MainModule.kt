package com.medic.net.main.di

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import com.medic.net.core.lifecycle.CancelStrategy
import com.medic.net.dagger.scope.PerActivity
import com.medic.net.main.presentation.MainNavigator
import com.medic.net.main.presentation.MainView
import com.medic.net.main.ui.MainActivity
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