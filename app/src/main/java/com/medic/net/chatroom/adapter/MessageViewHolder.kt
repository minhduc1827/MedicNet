package com.medic.net.chatroom.adapter

import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.view.isVisible
import com.medic.net.chatroom.viewmodel.MessageViewModel
import com.medic.net.util.extensions.setVisible
import com.medic.net.widget.emoji.EmojiReactionListener
import chat.rocket.core.model.isSystemMessage
import kotlinx.android.synthetic.main.avatar.view.*
import kotlinx.android.synthetic.main.item_message.view.*

class MessageViewHolder(
    itemView: View,
    listener: ActionsListener,
    reactionListener: EmojiReactionListener? = null
) : BaseViewHolder<MessageViewModel>(itemView, listener, reactionListener) {

    init {
        with(itemView) {
            setupActionMenu(message_container)
            text_content.movementMethod = LinkMovementMethod()
        }
    }

    override fun bindViews(data: MessageViewModel) {
        with(itemView) {
            if (data.isFirstUnread) new_messages_notif.visibility = View.VISIBLE
            else new_messages_notif.visibility = View.GONE

            text_message_time.text = data.time
            text_sender.text = data.senderName
            text_content.text = data.content
            image_avatar.setImageURI(data.avatar)
            text_content.setTextColor(
                if (data.isTemporary) Color.GRAY else Color.BLACK
            )
            data.message.let {
                text_edit_indicator.isVisible = it.isSystemMessage() && it.editedBy != null
                image_star_indicator.isVisible = it.starred?.isNotEmpty() ?: false
            }
        }
    }
}