package com.medic.net.chatroom.viewmodel.suggestion

import com.medic.net.widget.autocompletion.model.SuggestionModel
import chat.rocket.common.model.UserStatus

class PeopleSuggestionViewModel(val imageUri: String?,
                                text: String,
                                val username: String,
                                val name: String,
                                val status: UserStatus?,
                                pinned: Boolean = false,
                                searchList: List<String>) : SuggestionModel(text, searchList, pinned) {

    override fun toString(): String {
        return "PeopleSuggestionViewModel(imageUri='$imageUri', username='$username', name='$name', status=$status, pinned=$pinned)"
    }
}