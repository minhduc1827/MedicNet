package com.medicnet.android.server.presentation

import android.content.Intent
import com.medicnet.android.authentication.ui.newServerIntent
import com.medicnet.android.main.ui.MainActivity
import com.medicnet.android.server.ui.ChangeServerActivity

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