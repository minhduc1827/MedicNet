package com.medic.net.settings.di

import android.arch.lifecycle.LifecycleOwner
import com.medic.net.core.lifecycle.CancelStrategy
import com.medic.net.dagger.scope.PerFragment
import com.medic.net.settings.presentation.SettingsView
import com.medic.net.settings.ui.SettingsFragment
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