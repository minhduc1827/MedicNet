package com.medic.net.pinnedmessages.presentation

import com.medic.net.chatroom.viewmodel.BaseViewModel
import com.medic.net.core.behaviours.LoadingView
import com.medic.net.core.behaviours.MessageView

interface PinnedMessagesView : MessageView, LoadingView {

    /**
     * Show list of pinned messages for the current room.
     *
     * @param pinnedMessages The list of pinned messages.
     */
    fun showPinnedMessages(pinnedMessages: List<BaseViewModel<*>>)
}