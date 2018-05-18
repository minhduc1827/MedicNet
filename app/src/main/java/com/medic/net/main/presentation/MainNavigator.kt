package com.medic.net.main.presentation

import com.medic.net.R
import com.medic.net.authentication.ui.newServerIntent
import com.medic.net.chatroom.ui.chatRoomIntent
import com.medic.net.chatrooms.ui.ChatRoomsFragment
import com.medic.net.main.ui.MainActivity
import com.medic.net.profile.ui.ProfileFragment
import com.medic.net.server.ui.changeServerIntent
import com.medic.net.settings.ui.SettingsFragment
import com.medic.net.util.extensions.addFragment

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