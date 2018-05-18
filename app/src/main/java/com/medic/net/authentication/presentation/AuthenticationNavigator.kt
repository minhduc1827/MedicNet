package com.medic.net.authentication.presentation

import android.content.Intent
import com.medic.net.R
import com.medic.net.authentication.domain.model.LoginDeepLinkInfo
import com.medic.net.authentication.login.ui.LoginFragment
import com.medic.net.authentication.registerusername.ui.RegisterUsernameFragment
import com.medic.net.authentication.resetpassword.ui.ResetPasswordFragment
import com.medic.net.authentication.signup.ui.SignupFragment
import com.medic.net.authentication.twofactor.ui.TwoFAFragment
import com.medic.net.authentication.ui.AuthenticationActivity
import com.medic.net.authentication.ui.newServerIntent
import com.medic.net.main.ui.MainActivity
import com.medic.net.server.ui.changeServerIntent
import com.medic.net.util.extensions.addFragmentBackStack
import com.medic.net.util.extensions.toPreviousView
import com.medic.net.webview.ui.webViewIntent

class AuthenticationNavigator(internal val activity: AuthenticationActivity) {

    fun toLogin() {
        activity.addFragmentBackStack("LoginFragment", R.id.fragment_container) {
            LoginFragment.newInstance()
        }
    }

    fun toLogin(deepLinkInfo: LoginDeepLinkInfo) {
        activity.addFragmentBackStack("LoginFragment", R.id.fragment_container) {
            LoginFragment.newInstance(deepLinkInfo)
        }
    }

    fun toPreviousView() {
        activity.toPreviousView()
    }

    fun toTwoFA(username: String, password: String) {
        activity.addFragmentBackStack("TwoFAFragment", R.id.fragment_container) {
            TwoFAFragment.newInstance(username, password)
        }
    }

    fun toSignUp() {
        activity.addFragmentBackStack("SignupFragment", R.id.fragment_container) {
            SignupFragment.newInstance()
        }
    }

    fun toForgotPassword() {
        activity.addFragmentBackStack("ResetPasswordFragment", R.id.fragment_container) {
            ResetPasswordFragment.newInstance()
        }
    }

    fun toWebPage(url: String) {
        activity.startActivity(activity.webViewIntent(url))
        activity.overridePendingTransition(R.anim.slide_up, R.anim.hold)
    }

    fun toRegisterUsername(userId: String, authToken: String) {
        activity.addFragmentBackStack("RegisterUsernameFragment", R.id.fragment_container) {
            RegisterUsernameFragment.newInstance(userId, authToken)
        }
    }

    fun toChatList() {
        activity.startActivity(Intent(activity, MainActivity::class.java))
        activity.finish()
    }

    fun toChatList(serverUrl: String) {
        activity.startActivity(activity.changeServerIntent(serverUrl))
        activity.finish()
    }

    fun toServerScreen() {
        activity.startActivity(activity.newServerIntent())
        activity.finish()
    }
}