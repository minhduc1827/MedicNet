package com.medicnet.android.authentication.server.presentation

import com.medicnet.android.core.behaviours.LoadingView
import com.medicnet.android.core.behaviours.MessageView

interface ServerView : LoadingView, MessageView, VersionCheckView {

    /**
     * Shows an invalid server URL message.
     */
    fun showInvalidServerUrlMessage()
}