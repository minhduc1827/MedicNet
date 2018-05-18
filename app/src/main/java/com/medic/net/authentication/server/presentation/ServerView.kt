package com.medic.net.authentication.server.presentation

import com.medic.net.core.behaviours.LoadingView
import com.medic.net.core.behaviours.MessageView

interface ServerView : LoadingView, MessageView, VersionCheckView {

    /**
     * Shows an invalid server URL message.
     */
    fun showInvalidServerUrlMessage()
}