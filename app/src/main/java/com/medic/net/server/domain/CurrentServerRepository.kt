package com.medic.net.server.domain

interface CurrentServerRepository {
    fun save(url: String)
    fun get(): String?
    fun clear()
}