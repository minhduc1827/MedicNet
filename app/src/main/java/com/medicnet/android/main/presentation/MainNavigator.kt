package com.medicnet.android.main.presentation

import com.medicnet.android.R
import com.medicnet.android.authentication.ui.newServerIntent
import com.medicnet.android.chatroom.ui.chatRoomIntent
import com.medicnet.android.chatrooms.ui.ChatRoomsFragment
import com.medicnet.android.main.ui.MainActivity
import com.medicnet.android.profile.ui.ProfileFragment
import com.medicnet.android.server.ui.changeServerIntent
import com.medicnet.android.settings.ui.SettingsFragment
import com.medicnet.android.util.extensions.addFragment

class MainNavigator(internal val activity: MainActivity) {

    fun toChatList() {
        activity.addFragment("ChatRoomsFragment", R.id.fragment_container) {
            ChatRoomsFragment.newInstance()
        }
    }

    fun toUserProfile() {
        activity.addFragment("ProfileFragment", R.id.fragment_container) {
            ProfileFragment.newInstance()
        }
    }

    fun toSettings() {
        activity.addFragment("SettingsFragment", R.id.fragment_container) {
            SettingsFragment.newInstance()
        }
    }

    fun toChatRoom(chatRoomId: String,
                   chatRoomName: String,
                   chatRoomType: String,
                   isChatRoomReadOnly: Boolean,
                   chatRoomLastSeen: Long,
                   isChatRoomSubscribed: Boolean,
                   isChatRoomCreator: Boolean) {
        activity.startActivity(activity.chatRoomIntent(chatRoomId, chatRoomName, chatRoomType,
                isChatRoomReadOnly, chatRoomLastSeen, isChatRoomSubscribed, isChatRoomCreator))
        activity.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
    }

    fun toNewServer(serverUrl: String? = null) {
        activity.startActivity(activity.changeServerIntent(serverUrl))
        activity.finish()
    }

    fun toServerScreen() {
        activity.startActivity(activity.newServerIntent())
    }
}