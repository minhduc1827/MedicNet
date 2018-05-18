package com.medic.net.authentication.registerusername.presentation

import com.medic.net.core.behaviours.LoadingView
import com.medic.net.core.behaviours.MessageView

interface RegisterUsernameView : LoadingView, MessageView {

    /**
     * Alerts the user about a blank username.
     */
    fun alertBlankUsername()
}