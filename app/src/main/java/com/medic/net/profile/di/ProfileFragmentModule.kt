package com.medic.net.profile.di

import android.arch.lifecycle.LifecycleOwner
import com.medic.net.dagger.scope.PerFragment
import com.medic.net.profile.presentation.ProfileView
import com.medic.net.profile.ui.ProfileFragment
import dagger.Module
import dagger.Provides

@Module
@PerFragment
class ProfileFragmentModule {

    @Provides
    fun profileView(frag: ProfileFragment): ProfileView {
        return frag
    }

    @Provides
    fun provideLifecycleOwner(frag: ProfileFragment): LifecycleOwner {
        return frag
    }
}