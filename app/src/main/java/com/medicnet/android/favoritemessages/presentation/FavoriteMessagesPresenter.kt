package com.medicnet.android.favoritemessages.presentation

import chat.rocket.common.RocketChatException
import chat.rocket.common.util.ifNull
import chat.rocket.core.internal.rest.getFavoriteMessages
import com.medicnet.android.chatroom.viewmodel.ViewModelMapper
import com.medicnet.android.core.lifecycle.CancelStrategy
import com.medicnet.android.server.domain.ChatRoomsInteractor
import com.medicnet.android.server.domain.GetCurrentServerInteractor
import com.medicnet.android.server.infraestructure.RocketChatClientFactory
import com.medicnet.android.util.extensions.launchUI
import timber.log.Timber
import javax.inject.Inject

class FavoriteMessagesPresenter @Inject constructor(
    private val view: FavoriteMessagesView,
    private val strategy: CancelStrategy,
    private val serverInteractor: GetCurrentServerInteractor,
    private val roomsInteractor: ChatRoomsInteractor,
    private val mapper: ViewModelMapper,
    factory: RocketChatClientFactory
) {
    private val client = factory.create(serverInteractor.get()!!)
    private var offset: Int = 0

    /**
     * Loads all favorite messages for room. the given room id.
     *
     * @param roomId The id of the room to get its favorite messages.
     */
    fun loadFavoriteMessages(roomId: String) {
        launchUI(strategy) {
            try {
                val serverUrl = serverInteractor.get()!!
                val chatRoom = roomsInteractor.getById(serverUrl, roomId)
                chatRoom?.let { room ->
                    view.showLoading()
                    val favoriteMessages =
                        client.getFavoriteMessages(roomId, room.type, offset)
                    offset = favoriteMessages.offset.toInt()
                    val messageList = mapper.map(favoriteMessages.result)
                    view.showFavoriteMessages(messageList)
                    view.hideLoading()
                }.ifNull {
                    Timber.e("Couldn't find a room with id: $roomId at current server.")
                }
            } catch (e: RocketChatException) {
                Timber.e(e)
            }
        }
    }
}