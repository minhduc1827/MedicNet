package com.medicnet.android.authentication.resetpassword.presentation

import com.medicnet.android.core.behaviours.LoadingView
import com.medicnet.android.core.behaviours.MessageView

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