package com.medic.net.members.presentation

import com.medic.net.chatroom.ui.ChatRoomActivity
import com.medic.net.members.ui.newInstance

class MembersNavigator(internal val activity: ChatRoomActivity) {

    fun toMemberDetails(avatarUri: String, realName: String, username: String, email: String, utcOffset: String) {
        activity.apply {
            newInstance(avatarUri, realName, username, email, utcOffset)
                .show(supportFragmentManager, "MemberBottomSheetFragment")
        }
    }
}
