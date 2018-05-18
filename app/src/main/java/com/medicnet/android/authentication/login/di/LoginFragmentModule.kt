package com.medicnet.android.authentication.login.di

import android.arch.lifecycle.LifecycleOwner
import com.medicnet.android.authentication.login.presentation.LoginView
import com.medicnet.android.authentication.login.ui.LoginFragment
import com.medicnet.android.core.lifecycle.CancelStrategy
import com.medicnet.android.dagger.scope.PerFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
@PerFragment
class LoginFragmentModule {

    @Provides
    fun loginView(frag: LoginFragment): LoginView {
        return frag
    }

    @Provides
    fun provideLifecycleOwner(frag: LoginFragment): LifecycleOwner {
        return frag
    }

    @Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}