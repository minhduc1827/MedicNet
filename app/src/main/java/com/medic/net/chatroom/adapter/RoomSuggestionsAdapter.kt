package com.medic.net.chatroom.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.medic.net.R
import com.medic.net.chatroom.adapter.RoomSuggestionsAdapter.RoomSuggestionsViewHolder
import com.medic.net.chatroom.viewmodel.suggestion.ChatRoomSuggestionViewModel
import com.medic.net.widget.autocompletion.model.SuggestionModel
import com.medic.net.widget.autocompletion.ui.BaseSuggestionViewHolder
import com.medic.net.widget.autocompletion.ui.SuggestionsAdapter

class RoomSuggestionsAdapter : SuggestionsAdapter<RoomSuggestionsViewHolder>("#") {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomSuggestionsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.suggestion_room_item, parent,
                false)
        return RoomSuggestionsViewHolder(view)
    }

    class RoomSuggestionsViewHolder(view: View) : BaseSuggestionViewHolder(view) {

        override fun bind(item: SuggestionModel, itemClickListener: SuggestionsAdapter.ItemClickListener?) {
            item as ChatRoomSuggestionViewModel
            with(itemView) {
                val fullname = itemView.findViewById<TextView>(R.id.text_fullname)
                val name = itemView.findViewById<TextView>(R.id.text_name)
                name.text = item.name
                fullname.text = item.fullName
                setOnClickListener {
                    itemClickListener?.onClick(item)
                }
            }
        }
    }
}