package com.medic.net.authentication.login.di

import android.arch.lifecycle.LifecycleOwner
import com.medic.net.authentication.login.presentation.LoginView
import com.medic.net.authentication.login.ui.LoginFragment
import com.medic.net.core.lifecycle.CancelStrategy
import com.medic.net.dagger.scope.PerFragment
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