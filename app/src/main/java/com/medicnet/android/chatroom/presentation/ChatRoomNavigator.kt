package com.medicnet.android.chatroom.presentation

import com.medicnet.android.R
import com.medicnet.android.chatroom.ui.chatRoomIntent
import com.medicnet.android.main.ui.MainActivity
import com.medicnet.android.members.ui.newInstance
import com.medicnet.android.server.ui.changeServerIntent
import com.medicnet.android.util.extensions.addFragmentBackStack

class ChatRoomNavigator(internal val activity: MainActivity) {

    fun toMembersList(chatRoomId: String, chatRoomType: String) {
        activity.addFragmentBackStack("MembersFragment", R.id.fragment_container) {
            newInstance(chatRoomId, chatRoomType)
        }
    }

    fun toPinnedMessageList(chatRoomId: String, chatRoomType: String) {
        activity.addFragmentBackStack("PinnedMessages", R.id.fragment_container) {
            com.medicnet.android.pinnedmessages.ui.newInstance(chatRoomId, chatRoomType)
        }
    }

    fun toFavoriteMessageList(chatRoomId: String, chatRoomType: String) {
        activity.addFragmentBackStack("FavoriteMessages", R.id.fragment_container) {
            com.medicnet.android.favoritemessages.ui.newInstance(chatRoomId, chatRoomType)
        }
    }

    fun toNewServer() {
        activity.startActivity(activity.changeServerIntent())
        activity.finish()
    }

    fun toDirectMessage(chatRoomId: String,
                        chatRoomName: String,
                        chatRoomType: String,
                        isChatRoomReadOnly: Boolean,
                        chatRoomLastSeen: Long,
                        isChatRoomSubscribed: Boolean,
                        isChatRoomCreator: Boolean,
                        chatRoomMessage: String) {
        activity.startActivity(activity.chatRoomIntent(chatRoomId, chatRoomName, chatRoomType,
            isChatRoomReadOnly, chatRoomLastSeen, isChatRoomSubscribed, isChatRoomCreator, chatRoomMessage))
        activity.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
    }
}