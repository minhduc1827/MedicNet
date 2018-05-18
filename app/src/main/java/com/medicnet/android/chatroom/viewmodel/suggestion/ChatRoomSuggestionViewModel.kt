package com.medicnet.android.chatroom.viewmodel.suggestion

import com.medicnet.android.widget.autocompletion.model.SuggestionModel

class ChatRoomSuggestionViewModel(text: String,
                                  val fullName: String,
                                  val name: String,
                                  searchList: List<String>) : SuggestionModel(text, searchList, false) {
}