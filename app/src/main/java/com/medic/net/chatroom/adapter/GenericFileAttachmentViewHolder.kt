package com.medic.net.chatroom.adapter

import android.content.Intent
import android.net.Uri
import android.view.View
import com.medic.net.chatroom.viewmodel.GenericFileAttachmentViewModel
import com.medic.net.util.extensions.content
import com.medic.net.widget.emoji.EmojiReactionListener
import chat.rocket.common.util.ifNull
import kotlinx.android.synthetic.main.item_file_attachment.view.*

class GenericFileAttachmentViewHolder(itemView: View,
                                      listener: ActionsListener,
                                      reactionListener: EmojiReactionListener? = null)
    : BaseViewHolder<GenericFileAttachmentViewModel>(itemView, listener, reactionListener) {

    init {
        with(itemView) {
            setupActionMenu(file_attachment_container)
        }
    }

    override fun bindViews(data: GenericFileAttachmentViewModel) {
        with(itemView) {
            text_file_name.content = data.attachmentTitle

            text_file_name.setOnClickListener {
                it.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(data.attachmentUrl)))
            }
        }
    }
}