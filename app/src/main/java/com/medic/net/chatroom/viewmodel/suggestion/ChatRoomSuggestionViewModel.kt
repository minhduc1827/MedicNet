package com.medic.net.chatroom.viewmodel.suggestion

import com.medic.net.widget.autocompletion.model.SuggestionModel

class ChatRoomSuggestionViewModel(text: String,
                                  val fullName: String,
                                  val name: String,
                                  searchList: List<String>) : SuggestionModel(text, searchList, false) {
}