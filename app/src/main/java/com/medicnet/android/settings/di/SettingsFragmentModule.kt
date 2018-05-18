package com.medicnet.android.settings.di

import android.arch.lifecycle.LifecycleOwner
import com.medicnet.android.core.lifecycle.CancelStrategy
import com.medicnet.android.dagger.scope.PerFragment
import com.medicnet.android.settings.presentation.SettingsView
import com.medicnet.android.settings.ui.SettingsFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
@PerFragment
class SettingsFragmentModule {
    @Provides
    fun settingsView(frag: SettingsFragment): SettingsView {
        return frag
    }

    @Provides
    fun settingsLifecycleOwner(frag: SettingsFragment): LifecycleOwner {
        return frag
    }

    @Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}