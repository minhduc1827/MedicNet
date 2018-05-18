package com.medicnet.android.settings.password.di

import android.arch.lifecycle.LifecycleOwner
import com.medicnet.android.core.lifecycle.CancelStrategy
import com.medicnet.android.dagger.scope.PerFragment
import com.medicnet.android.settings.password.presentation.PasswordView
import com.medicnet.android.settings.password.ui.PasswordFragment
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
