package com.medic.net.chatroom.adapter

import android.view.View
import com.medic.net.chatroom.viewmodel.VideoAttachmentViewModel
import com.medic.net.player.PlayerActivity
import com.medic.net.util.extensions.setVisible
import com.medic.net.widget.emoji.EmojiReactionListener
import kotlinx.android.synthetic.main.message_attachment.view.*

class VideoAttachmentViewHolder(itemView: View,
                                listener: ActionsListener,
                                reactionListener: EmojiReactionListener? = null)
    : BaseViewHolder<VideoAttachmentViewModel>(itemView, listener, reactionListener) {

    init {
        with(itemView) {
            setupActionMenu(attachment_container)
            image_attachment.setVisible(false)
            audio_video_attachment.setVisible(true)
        }
    }

    override fun bindViews(data: VideoAttachmentViewModel) {
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