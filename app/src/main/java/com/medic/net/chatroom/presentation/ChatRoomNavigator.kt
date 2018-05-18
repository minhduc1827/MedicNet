package com.medic.net.chatroom.presentation

import com.medic.net.R
import com.medic.net.chatroom.ui.ChatRoomActivity
import com.medic.net.chatroom.ui.chatRoomIntent
import com.medic.net.members.ui.newInstance
import com.medic.net.server.ui.changeServerIntent
import com.medic.net.util.extensions.addFragmentBackStack

class ChatRoomNavigator(internal val activity: ChatRoomActivity) {

    fun toMembersList(chatRoomId: String, chatRoomType: String) {
        activity.addFragmentBackStack("MembersFragment", R.id.fragment_container) {
            newInstance(chatRoomId, chatRoomType)
        }
    }

    fun toPinnedMessageList(chatRoomId: String, chatRoomType: String) {
        activity.addFragmentBackStack("PinnedMessages", R.id.fragment_container) {
            com.medic.net.pinnedmessages.ui.newInstance(chatRoomId, chatRoomType)
        }
    }

    fun toFavoriteMessageList(chatRoomId: String, chatRoomType: String) {
        activity.addFragmentBackStack("FavoriteMessages", R.id.fragment_container) {
            com.medic.net.favoritemessages.ui.newInstance(chatRoomId, chatRoomType)
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