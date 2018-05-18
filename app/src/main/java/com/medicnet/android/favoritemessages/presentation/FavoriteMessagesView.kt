package com.medicnet.android.favoritemessages.presentation

import com.medicnet.android.chatroom.viewmodel.BaseViewModel
import com.medicnet.android.core.behaviours.LoadingView
import com.medicnet.android.core.behaviours.MessageView

interface FavoriteMessagesView : MessageView, LoadingView {

    /**
     * Shows the list of favorite messages for the current room.
     *
     * @param favoriteMessages The list of favorite messages to show.
     */
    fun showFavoriteMessages(favoriteMessages: List<BaseViewModel<*>>)
}