package com.medicnet.android.authentication.di

import com.medicnet.android.authentication.presentation.AuthenticationNavigator
import com.medicnet.android.authentication.ui.AuthenticationActivity
import com.medicnet.android.dagger.scope.PerActivity
import dagger.Module
import dagger.Provides

@Module
class AuthenticationModule {

    @Provides
    @PerActivity
    fun provideAuthenticationNavigator(activity: AuthenticationActivity) = AuthenticationNavigator(activity)
}