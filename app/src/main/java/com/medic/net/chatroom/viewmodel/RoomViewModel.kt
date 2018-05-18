package com.medic.net.chatroom.viewmodel

import chat.rocket.core.model.ChatRoomRole

data class RoomViewModel(
    val roles: List<ChatRoomRole>,
    val isBroadcast: Boolean = false
)