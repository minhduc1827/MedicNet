package com.medic.net.server.infraestructure

import com.medic.net.server.domain.ChatRoomsRepository
import chat.rocket.core.model.ChatRoom

class MemoryChatRoomsRepository : ChatRoomsRepository {

    val cache = HashMap<String, List<ChatRoom>>()

    override fun save(url: String, chatRooms: List<ChatRoom>) {
        //TODO: should diff the existing chatrooms and new chatroom dataset
        cache[url] = chatRooms
    }

    override fun get(url: String): List<ChatRoom> = cache[url] ?: emptyList()
}