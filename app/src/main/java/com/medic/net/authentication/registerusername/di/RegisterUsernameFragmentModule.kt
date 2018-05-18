package com.medic.net.authentication.registerusername.di

import android.arch.lifecycle.LifecycleOwner
import com.medic.net.authentication.registerusername.presentation.RegisterUsernameView
import com.medic.net.authentication.registerusername.ui.RegisterUsernameFragment
import com.medic.net.core.lifecycle.CancelStrategy
import com.medic.net.dagger.scope.PerFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
@PerFragment
class RegisterUsernameFragmentModule {

    @Provides
    fun registerUsernameView(frag: RegisterUsernameFragment): RegisterUsernameView {
        return frag
    }

    @Provides
    fun provideLifecycleOwner(frag: RegisterUsernameFragment): LifecycleOwner {
        return frag
    }

    @Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}