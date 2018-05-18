package com.medic.net.server.infraestructure

import com.medic.net.server.domain.ActiveUsersRepository
import chat.rocket.common.model.User

class MemoryActiveUsersRepository : ActiveUsersRepository {

    val cache = HashMap<String, List<User>>()

    override fun save(url: String, activeUsers: List<User>) {
        cache[url] = activeUsers
    }

    override fun get(url: String): List<User> = cache[url] ?: emptyList()
}