package com.medic.net.settings.password.di

import android.arch.lifecycle.LifecycleOwner
import com.medic.net.core.lifecycle.CancelStrategy
import com.medic.net.dagger.scope.PerFragment
import com.medic.net.settings.password.presentation.PasswordView
import com.medic.net.settings.password.ui.PasswordFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
@PerFragment
class PasswordFragmentModule {
    @Provides
    fun passwordView(frag: PasswordFragment): PasswordView {
        return frag
    }

    @Provides
    fun settingsLifecycleOwner(frag: PasswordFragment): LifecycleOwner {
        return frag
    }

    @Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}
