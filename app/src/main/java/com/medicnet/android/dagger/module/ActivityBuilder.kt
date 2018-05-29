package com.medicnet.android.dagger.module

import com.medicnet.android.authentication.di.AuthenticationModule
import com.medicnet.android.authentication.login.di.LoginFragmentProvider
import com.medicnet.android.authentication.registerusername.di.RegisterUsernameFragmentProvider
import com.medicnet.android.authentication.resetpassword.di.ResetPasswordFragmentProvider
import com.medicnet.android.authentication.server.di.ServerFragmentProvider
import com.medicnet.android.authentication.signup.di.SignupFragmentProvider
import com.medicnet.android.authentication.twofactor.di.TwoFAFragmentProvider
import com.medicnet.android.authentication.ui.AuthenticationActivity
import com.medicnet.android.chatroom.di.ChatRoomFragmentProvider
import com.medicnet.android.chatroom.di.ChatRoomModule
import com.medicnet.android.chatroom.di.FavoriteMessagesFragmentProvider
import com.medicnet.android.chatroom.di.PinnedMessagesFragmentProvider
import com.medicnet.android.chatroom.ui.ChatRoomActivity
import com.medicnet.android.chatrooms.di.ChatRoomsFragmentProvider
import com.medicnet.android.dagger.scope.PerActivity
import com.medicnet.android.main.di.MainModule
import com.medicnet.android.main.ui.MainActivity
import com.medicnet.android.members.di.MembersFragmentProvider
import com.medicnet.android.profile.di.ProfileFragmentProvider
import com.medicnet.android.server.di.ChangeServerModule
import com.medicnet.android.server.ui.ChangeServerActivity
import com.medicnet.android.settings.password.di.PasswordFragmentProvider
import com.medicnet.android.settings.password.ui.PasswordActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @PerActivity
    @ContributesAndroidInjector(modules = [AuthenticationModule::class,
        ServerFragmentProvider::class,
        LoginFragmentProvider::class,
//        SelectOrganizationProvider::class,
        RegisterUsernameFragmentProvider::class,
        ResetPasswordFragmentProvider::class,
        SignupFragmentProvider::class,
        TwoFAFragmentProvider::class
    ])
    abstract fun bindAuthenticationActivity(): AuthenticationActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [MainModule::class,
        ChatRoomsFragmentProvider::class,
        ChatRoomFragmentProvider::class, //add chat fragment to load chat when clicking item recycler
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