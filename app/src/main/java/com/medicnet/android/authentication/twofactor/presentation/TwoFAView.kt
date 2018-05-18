package com.medicnet.android.authentication.twofactor.presentation

import com.medicnet.android.core.behaviours.LoadingView
import com.medicnet.android.core.behaviours.MessageView

interface TwoFAView : LoadingView, MessageView {

    /**
     * Alerts the user about a blank Two Factor Authentication code.
     */
    fun alertBlankTwoFactorAuthenticationCode()

    /**
     * Alerts the user about an invalid inputted Two Factor Authentication code.
     */
    fun alertInvalidTwoFactorAuthenticationCode()
}