package com.medicnet.android.chatroom.adapter

import android.net.Uri
import android.view.View
import com.medicnet.android.chatroom.viewmodel.UrlPreviewViewModel
import com.medicnet.android.util.extensions.content
import com.medicnet.android.util.extensions.openTabbedUrl
import com.medicnet.android.util.extensions.setVisible
import com.medicnet.android.widget.emoji.EmojiReactionListener
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