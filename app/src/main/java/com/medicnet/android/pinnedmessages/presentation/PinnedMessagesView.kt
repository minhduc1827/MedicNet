package com.medicnet.android.pinnedmessages.presentation

import com.medicnet.android.chatroom.viewmodel.BaseViewModel
import com.medicnet.android.core.behaviours.LoadingView
import com.medicnet.android.core.behaviours.MessageView

interface PinnedMessagesView : MessageView, LoadingView {

    /**
     * Show list of pinned messages for the current room.
     *
     * @param pinnedMessages The list of pinned messages.
     */
    fun showPinnedMessages(pinnedMessages: List<BaseViewModel<*>>)
}