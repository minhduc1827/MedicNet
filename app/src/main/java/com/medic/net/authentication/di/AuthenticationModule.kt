package com.medic.net.authentication.di

import com.medic.net.authentication.presentation.AuthenticationNavigator
import com.medic.net.authentication.ui.AuthenticationActivity
import com.medic.net.dagger.scope.PerActivity
import dagger.Module
import dagger.Provides

@Module
class AuthenticationModule {

    @Provides
    @PerActivity
    fun provideAuthenticationNavigator(activity: AuthenticationActivity) = AuthenticationNavigator(activity)
}