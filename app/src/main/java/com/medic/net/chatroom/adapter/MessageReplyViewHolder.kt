package com.medic.net.chatroom.adapter

import android.view.View
import com.medic.net.chatroom.viewmodel.MessageReplyViewModel
import com.medic.net.widget.emoji.EmojiReactionListener
import kotlinx.android.synthetic.main.item_message_reply.view.*

class MessageReplyViewHolder(
    itemView: View,
    listener: ActionsListener,
    reactionListener: EmojiReactionListener? = null,
    private val replyCallback: (roomName: String, permalink: String) -> Unit
) : BaseViewHolder<MessageReplyViewModel>(itemView, listener, reactionListener) {

    init {
        with(itemView) {
            setupActionMenu(itemView)
        }
    }

    override fun bindViews(data: MessageReplyViewModel) {
        with(itemView) {
            button_message_reply.setOnClickListener {
                with(data.rawData) {
                    replyCallback.invoke(roomName, permalink)
                }
            }
        }
    }
}