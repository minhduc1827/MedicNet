package com.medicnet.android.server.infraestructure

import chat.rocket.common.model.User
import com.medicnet.android.server.domain.ActiveUsersRepository

class MemoryActiveUsersRepository : ActiveUsersRepository {

    val cache = HashMap<String, List<User>>()

    override fun save(url: String, activeUsers: List<User>) {
        cache[url] = activeUsers
    }

    override fun get(url: String): List<User> = cache[url] ?: emptyList()
}