package com.medicnet.android.server.domain

interface CurrentServerRepository {
    fun save(url: String)
    fun get(): String?
    fun clear()
}