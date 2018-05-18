package com.medicnet.android.chatroom.viewmodel

interface BaseAttachmentViewModel<out T> : BaseViewModel<T> {
    val attachmentUrl: String
}