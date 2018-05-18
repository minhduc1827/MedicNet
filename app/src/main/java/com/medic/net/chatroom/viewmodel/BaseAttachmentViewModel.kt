package com.medic.net.chatroom.viewmodel

interface BaseAttachmentViewModel<out T> : BaseViewModel<T> {
    val attachmentUrl: String
}