package com.medic.net.chatroom.viewmodel

interface BaseFileAttachmentViewModel<out T> : BaseAttachmentViewModel<T> {
    val attachmentTitle: CharSequence
    val id: Long
}