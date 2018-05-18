package com.medicnet.android.chatroom.adapter

import DrawableHelper
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.medicnet.android.R
import com.medicnet.android.chatroom.adapter.PeopleSuggestionsAdapter.PeopleSuggestionViewHolder
import com.medicnet.android.chatroom.viewmodel.suggestion.PeopleSuggestionViewModel
import com.medicnet.android.util.extensions.setVisible
import com.medicnet.android.widget.autocompletion.model.SuggestionModel
import com.medicnet.android.widget.autocompletion.ui.BaseSuggestionViewHolder
import com.medicnet.android.widget.autocompletion.ui.SuggestionsAdapter

class PeopleSuggestionsAdapter(context: Context) : SuggestionsAdapter<PeopleSuggestionViewHolder>("@") {

    init {
        val allDescription = context.getString(R.string.suggest_all_description)
        val hereDescription = context.getString(R.string.suggest_here_description)
        val pinnedList = listOf(
                PeopleSuggestionViewModel(imageUri = null,
                        text = "all",
                        username = "all",
                        name = allDescription,
                        status = null,
                        pinned = false,
                        searchList = listOf("all")),
                PeopleSuggestionViewModel(imageUri = null,
                        text = "here",
                        username = "here",
                        name = hereDescription,
                        status = null,
                        pinned = false,
                        searchList = listOf("here"))
        )
        setPinnedSuggestions(pinnedList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleSuggestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.suggestion_member_item, parent,
                false)
        return PeopleSuggestionViewHolder(view)
    }

    class PeopleSuggestionViewHolder(view: View) : BaseSuggestionViewHolder(view) {

        override fun bind(item: SuggestionModel, itemClickListener: SuggestionsAdapter.ItemClickListener?) {
            item as PeopleSuggestionViewModel
            with(itemView) {
                val username = itemView.findViewById<TextView>(R.id.text_username)
                val name = itemView.findViewById<TextView>(R.id.text_name)
                val avatar = itemView.findViewById<SimpleDraweeView>(R.id.image_avatar)
                val statusView = itemView.findViewById<ImageView>(R.id.image_status)
                username.text = item.username
                name.text = item.name
                if (item.imageUri?.isEmpty() != false) {
                    avatar.setVisible(false)
                } else {
                    avatar.setVisible(true)
                    avatar.setImageURI(item.imageUri)
                }
                val status = item.status
                if (status != null) {
                    val statusDrawable = DrawableHelper.getUserStatusDrawable(status, itemView.context)
                    statusView.setImageDrawable(statusDrawable)
                } else {
                    statusView.setVisible(false)
                }
                setOnClickListener {
                    itemClickListener?.onClick(item)
                }
            }
        }
    }
}