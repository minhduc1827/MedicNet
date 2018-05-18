package com.medic.net.authentication.signup.presentation

import com.medic.net.core.behaviours.LoadingView
import com.medic.net.core.behaviours.MessageView

interface SignupView : LoadingView, MessageView {

    /**
     * Alerts the user about a blank name.
     */
    fun alertBlankName()

    /**
     * Alerts the user about a blank username.
     */
    fun alertBlankUsername()

    /**
     * Alerts the user about a empty password.
     */
    fun alertEmptyPassword()

    /**
     * Alerts the user about a blank email.
     */
    fun alertBlankEmail()
}