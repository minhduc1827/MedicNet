package com.medic.net.widget.autocompletion.repository

interface LocalSuggestionProvider {
    fun find(prefix: String)
}