package com.medicnet.android.members.presentation

import com.medicnet.android.chatroom.ui.ChatRoomActivity
import com.medicnet.android.members.ui.newInstance

class MembersNavigator(internal val activity: ChatRoomActivity) {

    fun toMemberDetails(avatarUri: String, realName: String, username: String, email: String, utcOffset: String) {
        activity.apply {
            newInstance(avatarUri, realName, username, email, utcOffset)
                    .show(supportFragmentManager, "MemberBottomSheetFragment")
        }
    }
}