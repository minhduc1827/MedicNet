package com.medic.net.chatroom.viewmodel

import com.medic.net.R
import com.medic.net.chatroom.domain.MessageReply
import chat.rocket.core.model.Message

data class MessageReplyViewModel(
    override val rawData: MessageReply,
    override val messageId: String,
    override var reactions: List<ReactionViewModel>,
    override var nextDownStreamMessage: BaseViewModel<*>?,
    override var preview: Message?,
    override var isTemporary: Boolean = false,
    override val message: Message
) : BaseViewModel<MessageReply> {
    override val viewType: Int
        get() = BaseViewModel.ViewType.MESSAGE_REPLY.viewType
    override val layoutId: Int
        get() = R.layout.item_message_reply
}