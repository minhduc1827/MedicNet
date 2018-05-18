package com.medic.net.authentication.signup.di

import android.arch.lifecycle.LifecycleOwner
import com.medic.net.authentication.signup.presentation.SignupView
import com.medic.net.authentication.signup.ui.SignupFragment
import com.medic.net.core.lifecycle.CancelStrategy
import com.medic.net.dagger.scope.PerFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
@PerFragment
class SignupFragmentModule {

    @Provides
    fun signupView(frag: SignupFragment): SignupView {
        return frag
    }

    @Provides
    fun provideLifecycleOwner(frag: SignupFragment): LifecycleOwner {
        return frag
    }

    @Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}