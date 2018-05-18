package com.medicnet.android.server.infraestructure

import chat.rocket.core.model.ChatRoom
import com.medicnet.android.server.domain.ChatRoomsRepository

class MemoryChatRoomsRepository : ChatRoomsRepository {

    val cache = HashMap<String, List<ChatRoom>>()

    override fun save(url: String, chatRooms: List<ChatRoom>) {
        //TODO: should diff the existing chatrooms and new chatroom dataset
        cache[url] = chatRooms
    }

    override fun get(url: String): List<ChatRoom> = cache[url] ?: emptyList()
}