package com.medic.net.authentication.resetpassword.presentation

import com.medic.net.core.behaviours.LoadingView
import com.medic.net.core.behaviours.MessageView

interface ResetPasswordView : LoadingView, MessageView {

    /**
     * Alerts the user about a blank email.
     */
    fun alertBlankEmail()

    /**
     * Alerts the user about a invalid email.
     */
    fun alertInvalidEmail()

    /**
     * Shows a successful email sent message.
     */
    fun emailSent()

    /**
     * Shows a message to update the server version in order to use an app feature.
     */
    fun updateYourServerVersion()
}