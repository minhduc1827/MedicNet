package com.medicnet.android.authentication.signup.presentation

import com.medicnet.android.core.behaviours.LoadingView
import com.medicnet.android.core.behaviours.MessageView

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

    /**
     * Alerts the user about a empty role.
     */
    fun alertEmptyRole()

    /**
     * Alerts the user about a empty organization.
     */
    fun alertEmptyOrganization()
}