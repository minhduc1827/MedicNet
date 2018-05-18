package com.medic.net.server.domain

interface TokenRepository : chat.rocket.core.TokenRepository {
    fun remove(url: String)
    fun clear()
}