package com.medicnet.android.main.presentation

import android.support.v4.app.Fragment
import com.medicnet.android.R
import com.medicnet.android.authentication.ui.newServerIntent
import com.medicnet.android.chatroom.ui.ChatRoomActivity
import com.medicnet.android.chatroom.ui.newInstance
import com.medicnet.android.chatrooms.ui.ChatRoomsFragment
import com.medicnet.android.contacts.ui.NewTeamFragment
import com.medicnet.android.main.ui.MainActivity
import com.medicnet.android.profile.ui.ProfileFragment
import com.medicnet.android.server.ui.changeServerIntent
import com.medicnet.android.settings.ui.SettingsFragment
import com.medicnet.android.util.LogUtil
import com.medicnet.android.util.extensions.addFragment
import com.medicnet.android.util.extensions.addOverlayFragment
import com.medicnet.android.util.extensions.removeFragment

class MainNavigator(internal val activity: MainActivity) {

    fun toChatList() {
//        Log.d("DucNM","toChatList>>"+activity.headerLayout.navigation_header_chat_room.id);
        activity.addFragment(ChatRoomsFragment.TAG, R.id.navigation_header_chat_room) {
            ChatRoomsFragment.newInstance()
        }
    }

    fun toUserProfile() {
        activity.addFragment("ProfileFragment", R.id.fragment_container) {
            ProfileFragment.newInstance()
        }
    }

    fun toNewTeam(): NewTeamFragment {
        return activity.addOverlayFragment(NewTeamFragment.TAG, R.id.fragment_entire_screen) {
            NewTeamFragment.newInstance()
        } as NewTeamFragment
    }

    fun removeFragment(fragment: Fragment) {
        activity.removeFragment(fragment)
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
        /* activity.startActivity(activity.chatRoomIntent(chatRoomId, chatRoomName, chatRoomType,
                 isChatRoomReadOnly, chatRoomLastSeen, isChatRoomSubscribed, isChatRoomCreator))
         activity.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)*/
        LogUtil.d("MainNavigator", "toChatRoom @chatRoomId= " + chatRoomId + " @chatRoomName= " + chatRoomName)
        activity.addFragment(ChatRoomActivity.TAG_CHAT_ROOM_FRAGMENT + "_" + chatRoomId, R.id.fragment_container) {
            newInstance(chatRoomId, chatRoomName, chatRoomType, isChatRoomReadOnly, chatRoomLastSeen,
                    isChatRoomSubscribed, isChatRoomCreator, null)
        }
    }

    fun toChatRoom(chatRoomId: String,
                   chatRoomName: String,
                   chatRoomType: String,
                   isChatRoomReadOnly: Boolean,
                   chatRoomLastSeen: Long,
                   isChatRoomSubscribed: Boolean,
                   isChatRoomCreator: Boolean,
                   avatarUrl: String,
                   isMyVault: Boolean) {
        LogUtil.d("MainNavigator", "toChatRoom @chatRoomId= " + chatRoomId + " @chatRoomName= " + chatRoomName + " @avatarUrl= " + avatarUrl)
        activity.addFragment(ChatRoomActivity.TAG_CHAT_ROOM_FRAGMENT + "_" + chatRoomId, R.id.fragment_container) {
            newInstance(chatRoomId, chatRoomName, chatRoomType, isChatRoomReadOnly, chatRoomLastSeen,
                    isChatRoomSubscribed, isChatRoomCreator, null, avatarUrl, isMyVault)
        }
    }

    fun toNewServer(serverUrl: String? = null) {
        activity.startActivity(activity.changeServerIntent(serverUrl))
        activity.finish()
    }

    fun toServerScreen() {
        activity.startActivity(activity.newServerIntent())
    }
}