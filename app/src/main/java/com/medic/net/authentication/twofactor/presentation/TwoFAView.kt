package com.medic.net.authentication.twofactor.presentation

import com.medic.net.core.behaviours.LoadingView
import com.medic.net.core.behaviours.MessageView

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