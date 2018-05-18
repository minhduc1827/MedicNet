package com.medicnet.android.server.domain

interface TokenRepository : chat.rocket.core.TokenRepository {
    fun remove(url: String)
    fun clear()
}