package com.medicnet.android.chatroom.adapter

import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.view.isVisible
import chat.rocket.core.model.isSystemMessage
import com.medicnet.android.R
import com.medicnet.android.chatroom.viewmodel.MessageViewModel
import com.medicnet.android.util.LogUtil
import com.medicnet.android.util.extensions.textContent
import com.medicnet.android.widget.emoji.EmojiReactionListener
import kotlinx.android.synthetic.main.avatar.view.*
import kotlinx.android.synthetic.main.item_message.view.*

class MessageViewHolder(
    itemView: View,
    listener: ActionsListener,
    reactionListener: EmojiReactionListener? = null
) : BaseViewHolder<MessageViewModel>(itemView, listener, reactionListener) {
    private val TAG: String = MessageViewHolder::class.java.simpleName
    init {
        with(itemView) {
            setupActionMenu(message_container)
            text_content.movementMethod = LinkMovementMethod()
        }
    }

    override fun bindViews(data: MessageViewModel) {
        with(itemView) {
            val unread = data.message.unread
            LogUtil.d(TAG, "bindViews @message= " + data.message);
            if (!data.dateDisplay.equals("")) {
                new_messages_notif.visibility = View.VISIBLE
                txvTimeSeperation.textContent = data.dateDisplay
            } else new_messages_notif.visibility = View.GONE
            if (unread != null) {
                if (unread)
                    imvMsgStatus.setImageResource(R.drawable.ic_unread)
                else {
                    imvMsgStatus.setImageResource(R.drawable.ic_read)
                }
            }
            text_message_time.text = data.time
            text_sender.text = if (data.message.sender != null) data.message.sender!!.name else data.senderName
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