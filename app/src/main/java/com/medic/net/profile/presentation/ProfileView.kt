package com.medic.net.profile.presentation

import com.medic.net.core.behaviours.LoadingView
import com.medic.net.core.behaviours.MessageView

interface ProfileView : LoadingView, MessageView {

    /**
     * Shows the user profile.
     *
     * @param avatarUrl The user avatar URL.
     * @param name The user display name.
     * @param username The user username.
     * @param email The user email.
     */
    fun showProfile(avatarUrl: String, name: String, username: String, email: String?)

    /**
     * Shows a profile update successfully message
     */
    fun showProfileUpdateSuccessfullyMessage()
}