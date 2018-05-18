package com.medic.net.dagger.module

import com.medic.net.authentication.di.AuthenticationModule
import com.medic.net.authentication.login.di.LoginFragmentProvider
import com.medic.net.authentication.registerusername.di.RegisterUsernameFragmentProvider
import com.medic.net.authentication.resetpassword.di.ResetPasswordFragmentProvider
import com.medic.net.authentication.server.di.ServerFragmentProvider
import com.medic.net.authentication.signup.di.SignupFragmentProvider
import com.medic.net.authentication.twofactor.di.TwoFAFragmentProvider
import com.medic.net.authentication.ui.AuthenticationActivity
import com.medic.net.chatroom.di.ChatRoomFragmentProvider
import com.medic.net.chatroom.di.ChatRoomModule
import com.medic.net.chatroom.di.FavoriteMessagesFragmentProvider
import com.medic.net.chatroom.di.PinnedMessagesFragmentProvider
import com.medic.net.chatroom.ui.ChatRoomActivity
import com.medic.net.chatrooms.di.ChatRoomsFragmentProvider
import com.medic.net.dagger.scope.PerActivity
import com.medic.net.main.di.MainModule
import com.medic.net.main.ui.MainActivity
import com.medic.net.members.di.MembersFragmentProvider
import com.medic.net.profile.di.ProfileFragmentProvider
import com.medic.net.server.di.ChangeServerModule
import com.medic.net.server.ui.ChangeServerActivity
import com.medic.net.settings.password.di.PasswordFragmentProvider
import com.medic.net.settings.password.ui.PasswordActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @PerActivity
    @ContributesAndroidInjector(modules = [AuthenticationModule::class,
        ServerFragmentProvider::class,
        LoginFragmentProvider::class,
        RegisterUsernameFragmentProvider::class,
        ResetPasswordFragmentProvider::class,
        SignupFragmentProvider::class,
        TwoFAFragmentProvider::class
    ])
    abstract fun bindAuthenticationActivity(): AuthenticationActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [MainModule::class,
        ChatRoomsFragmentProvider::class,
        ProfileFragmentProvider::class
    ])
    abstract fun bindMainActivity(): MainActivity

    @PerActivity
    @ContributesAndroidInjector(
        modules = [
            ChatRoomModule::class,
            ChatRoomFragmentProvider::class,
            MembersFragmentProvider::class,
            PinnedMessagesFragmentProvider::class,
            FavoriteMessagesFragmentProvider::class
        ]
    )
    abstract fun bindChatRoomActivity(): ChatRoomActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [PasswordFragmentProvider::class])
    abstract fun bindPasswordActivity(): PasswordActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [ChangeServerModule::class])
    abstract fun bindChangeServerActivity(): ChangeServerActivity
}