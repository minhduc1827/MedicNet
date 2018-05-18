package com.medic.net.chatroom.viewmodel.suggestion

import com.medic.net.widget.autocompletion.model.SuggestionModel

class CommandSuggestionViewModel(text: String,
                                 val description: String,
                                 searchList: List<String>) : SuggestionModel(text, searchList)