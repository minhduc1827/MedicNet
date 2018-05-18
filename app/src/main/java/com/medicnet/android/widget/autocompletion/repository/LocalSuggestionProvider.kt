package com.medicnet.android.widget.autocompletion.repository

interface LocalSuggestionProvider {
    fun find(prefix: String)
}