package com.medicnet.android.chatroom.adapter

import android.view.View
import com.medicnet.android.chatroom.viewmodel.AudioAttachmentViewModel
import com.medicnet.android.player.PlayerActivity
import com.medicnet.android.util.extensions.setVisible
import com.medicnet.android.widget.emoji.EmojiReactionListener
import kotlinx.android.synthetic.main.message_attachment.view.*

class AudioAttachmentViewHolder(itemView: View,
                                listener: ActionsListener,
                                reactionListener: EmojiReactionListener? = null)
    : BaseViewHolder<AudioAttachmentViewModel>(itemView, listener, reactionListener) {

    init {
        with(itemView) {
            setupActionMenu(attachment_container)
            image_attachment.setVisible(false)
            audio_video_attachment.setVisible(true)
        }
    }

    override fun bindViews(data: AudioAttachmentViewModel) {
        with(itemView) {
            file_name.text = data.attachmentTitle
            audio_video_attachment.setOnClickListener { view ->
                data.attachmentUrl.let { url ->
                    PlayerActivity.play(view.context, url)
                }
            }
        }
    }
}