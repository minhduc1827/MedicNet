package com.medic.net.widget.autocompletion.strategy

import com.medic.net.widget.autocompletion.model.SuggestionModel

interface CompletionStrategy {
    fun getItem(prefix: String, position: Int): SuggestionModel
    fun autocompleteItems(prefix: String): List<SuggestionModel>
    fun addAll(list: List<SuggestionModel>)
    fun addPinned(list: List<SuggestionModel>)
    fun size(): Int
}