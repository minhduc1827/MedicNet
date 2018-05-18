package com.medicnet.android.authentication.resetpassword.di

import android.arch.lifecycle.LifecycleOwner
import com.medicnet.android.authentication.resetpassword.presentation.ResetPasswordView
import com.medicnet.android.authentication.resetpassword.ui.ResetPasswordFragment
import com.medicnet.android.core.lifecycle.CancelStrategy
import com.medicnet.android.dagger.scope.PerFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

@Module
@PerFragment
class ResetPasswordFragmentModule {

    @Provides
    fun resetPasswordView(frag: ResetPasswordFragment): ResetPasswordView {
        return frag
    }

    @Provides
    fun provideLifecycleOwner(frag: ResetPasswordFragment): LifecycleOwner {
        return frag
    }

    @Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}