package com.medicnet.android.settings.password.presentation

import com.medicnet.android.core.behaviours.LoadingView

interface PasswordView: LoadingView {
    /**
     * Shows a message when a user's password is successfully updated
     */
    fun showPasswordSuccessfullyUpdatedMessage()

    /**
     * Shows a message when the user's password fails to update
     * @param error is a String containing the failure message
     */
    fun showPasswordFailsUpdateMessage(error : String?)
}
