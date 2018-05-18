package com.medicnet.android.chatroom.viewmodel.suggestion

import com.medicnet.android.widget.autocompletion.model.SuggestionModel

class CommandSuggestionViewModel(text: String,
                                 val description: String,
                                 searchList: List<String>) : SuggestionModel(text, searchList)