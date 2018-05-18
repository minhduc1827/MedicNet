package com.medicnet.android.authentication.registerusername.presentation

import com.medicnet.android.core.behaviours.LoadingView
import com.medicnet.android.core.behaviours.MessageView

interface RegisterUsernameView : LoadingView, MessageView {

    /**
     * Alerts the user about a blank username.
     */
    fun alertBlankUsername()
}