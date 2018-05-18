package com.medic.net.helper

import com.medic.net.server.domain.GetCurrentServerInteractor
import com.medic.net.server.domain.GetSettingsInteractor
import com.medic.net.server.domain.PublicSettings
import com.medic.net.server.domain.useRealName
import chat.rocket.common.model.RoomType
import chat.rocket.core.model.ChatRoom
import chat.rocket.core.model.Message
import javax.inject.Inject

class MessageHelper @Inject constructor(
    getSettingsInteractor: GetSettingsInteractor,
    serverInteractor: GetCurrentServerInteractor
) {
    private val currentServer = serverInteractor.get()!!
    private val settings: PublicSettings = getSettingsInteractor.get(currentServer)

    fun createPermalink(message: Message, chatRoom: ChatRoom): String {
        val type = when (chatRoom.type) {
            is RoomType.PrivateGroup -> "group"
            is RoomType.Channel -> "channel"
            is RoomType.DirectMessage -> "direct"
            is RoomType.Livechat -> "livechat"
            else -> "custom"
        }
        val name = if (settings.useRealName()) chatRoom.fullName ?: chatRoom.name else chatRoom.name
        return "[ ]($currentServer/$type/$name?msg=${message.id}) "
    }

    fun messageIdFromPermalink(permalink: String): String? {
        PERMALINK_REGEX.find(permalink.trim())?.let {
            if (it.groupValues.size == 5) {
                return it.groupValues[MESSAGE_ID]
            }
        }
        return null
    }

    fun roomNameFromPermalink(permalink: String): String? {
        PERMALINK_REGEX.find(permalink.trim())?.let {
            if (it.groupValues.size == 5) {
                return it.groupValues[ROOM_NAME]
            }
        }
        return null
    }

    fun roomTypeFromPermalink(permalink: String): String? {
        PERMALINK_REGEX.find(permalink.trim())?.let {
            if (it.groupValues.size == 5) {
                val type = it.groupValues[ROOM_TYPE]
                return when(type) {
                    "group" -> "p"
                    "channel" -> "c"
                    "direct" -> "d"
                    "livechat" -> "l"
                    else -> type
                }
            }
        }
        return null
    }

    companion object {
        private const val ROOM_TYPE = 2
        private const val ROOM_NAME = 3
        private const val MESSAGE_ID = 4
        val PERMALINK_REGEX = "(?:__|[*#])|\\[(.+?)\\]\\(.+?//.+?/(.+)/(.+)\\?.*=(.*)\\)".toRegex()
    }
}