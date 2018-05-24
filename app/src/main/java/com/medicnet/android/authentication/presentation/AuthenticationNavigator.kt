package com.medicnet.android.authentication.presentation

import android.content.Intent
import com.medicnet.android.R
import com.medicnet.android.authentication.domain.model.LoginDeepLinkInfo
import com.medicnet.android.authentication.login.ui.LoginFragment
import com.medicnet.android.authentication.registerusername.ui.RegisterUsernameFragment
import com.medicnet.android.authentication.resetpassword.ui.ResetPasswordFragment
import com.medicnet.android.authentication.selectorganization.ui.SelectOrganizationFragment
import com.medicnet.android.authentication.signup.ui.SignupFragment
import com.medicnet.android.authentication.twofactor.ui.TwoFAFragment
import com.medicnet.android.authentication.ui.AuthenticationActivity
import com.medicnet.android.authentication.ui.newServerIntent
import com.medicnet.android.main.ui.MainActivity
import com.medicnet.android.server.ui.changeServerIntent
import com.medicnet.android.util.extensions.addFragmentBackStack
import com.medicnet.android.util.extensions.toPreviousView
import com.medicnet.android.webview.ui.webViewIntent

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

    fun toSelectOrganization(json: String) {
        activity.addFragmentBackStack("SelectOrganizationFragment", R.id.fragment_container) {
            SelectOrganizationFragment.newInstance(json)
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