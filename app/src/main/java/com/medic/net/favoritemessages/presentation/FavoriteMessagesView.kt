package com.medic.net.favoritemessages.presentation

import com.medic.net.chatroom.viewmodel.BaseViewModel
import com.medic.net.core.behaviours.LoadingView
import com.medic.net.core.behaviours.MessageView

interface FavoriteMessagesView : MessageView, LoadingView {

    /**
     * Shows the list of favorite messages for the current room.
     *
     * @param favoriteMessages The list of favorite messages to show.
     */
    fun showFavoriteMessages(favoriteMessages: List<BaseViewModel<*>>)
}