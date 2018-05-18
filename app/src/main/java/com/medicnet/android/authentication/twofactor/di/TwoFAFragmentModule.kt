package com.medicnet.android.authentication.twofactor.di

import android.arch.lifecycle.LifecycleOwner
import com.medicnet.android.authentication.twofactor.presentation.TwoFAView
import com.medicnet.android.authentication.twofactor.ui.TwoFAFragment
import com.medicnet.android.core.lifecycle.CancelStrategy
import com.medicnet.android.dagger.scope.PerFragment
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
