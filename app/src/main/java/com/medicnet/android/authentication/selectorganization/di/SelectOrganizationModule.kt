package com.medicnet.android.authentication.selectorganization.di

import android.arch.lifecycle.LifecycleOwner
import com.medicnet.android.authentication.selectorganization.presentation.SelectOrganizationView
import com.medicnet.android.authentication.selectorganization.ui.SelectOrganizationFragment
import com.medicnet.android.core.lifecycle.CancelStrategy
import com.medicnet.android.dagger.scope.PerFragment
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.Job

/**
 * @author duc.nguyen
 * @since 5/24/2018
 */
@Module
@PerFragment
class SelectOrganizationModule {
    @Provides
    fun SelectOrganizationView(frag: SelectOrganizationFragment): SelectOrganizationView {
        return frag
    }

    @Provides
    fun provideLifecycleOwner(frag: SelectOrganizationFragment): LifecycleOwner {
        return frag
    }

    @Provides
    fun provideCancelStrategy(owner: LifecycleOwner, jobs: Job): CancelStrategy {
        return CancelStrategy(owner, jobs)
    }
}