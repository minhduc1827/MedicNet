package com.medic.net.chatroom.di

import com.medic.net.favoritemessages.ui.FavoriteMessagesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FavoriteMessagesFragmentProvider {

    @ContributesAndroidInjector(modules = [FavoriteMessagesFragmentModule::class])
    abstract fun provideFavoriteMessageFragment(): FavoriteMessagesFragment
}