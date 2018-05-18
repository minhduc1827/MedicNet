package com.medic.net.chatroom.adapter

import android.content.Intent
import android.net.Uri
import android.view.View
import com.medic.net.chatroom.viewmodel.UrlPreviewViewModel
import com.medic.net.util.extensions.content
import com.medic.net.util.extensions.openTabbedUrl
import com.medic.net.util.extensions.setVisible
import com.medic.net.widget.emoji.EmojiReactionListener
import kotlinx.android.synthetic.main.message_url_preview.view.*

class UrlPreviewViewHolder(itemView: View,
                           listener: ActionsListener,
                           reactionListener: EmojiReactionListener? = null)
    : BaseViewHolder<UrlPreviewViewModel>(itemView, listener, reactionListener) {

    init {
        with(itemView) {
            setupActionMenu(url_preview_layout)
        }
    }

    override fun bindViews(data: UrlPreviewViewModel) {
        with(itemView) {
            if (data.thumbUrl.isNullOrEmpty()) {
                image_preview.setVisible(false)
            } else {
                image_preview.setImageURI(data.thumbUrl)
                image_preview.setVisible(true)
            }
            text_host.content = data.hostname
            text_title.content = data.title
            text_description.content = data.description ?: ""

            url_preview_layout.setOnClickListener(onClickListener)
            text_host.setOnClickListener(onClickListener)
            text_title.setOnClickListener(onClickListener)
            image_preview.setOnClickListener(onClickListener)
            text_description.setOnClickListener(onClickListener)
        }
    }

    private val onClickListener = { view: View ->
        if (data != null) {
            view.openTabbedUrl(Uri.parse(data!!.rawData.url))
        }
    }
}