package com.medic.net.authentication.twofactor.di

import android.arch.lifecycle.LifecycleOwner
import com.medic.net.authentication.twofactor.presentation.TwoFAView
import com.medic.net.authentication.twofactor.ui.TwoFAFragment
import com.medic.net.core.lifecycle.CancelStrategy
import com.medic.net.dagger.scope.PerFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
@PerFragment
class TwoFAFragmentModule {

    @Provides
    fun loginView(frag: TwoFAFragment): TwoFAView {
        return frag
    }

    @Provides
    fun provideLifecycleOwner(frag: TwoFAFragment): LifecycleOwner {
        return frag
    }

    @Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}
