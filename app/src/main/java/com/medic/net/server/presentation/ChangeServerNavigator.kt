package com.medic.net.server.presentation

import android.content.Intent
import com.medic.net.authentication.ui.newServerIntent
import com.medic.net.main.ui.MainActivity
import com.medic.net.server.ui.ChangeServerActivity

class ChangeServerNavigator (internal val activity: ChangeServerActivity) {
    fun toServerScreen() {
        activity.startActivity(activity.newServerIntent())
        activity.finish()
    }

    fun toChatRooms() {
        activity.startActivity(Intent(activity, MainActivity::class.java))
        activity.finish()
    }

}