package com.medic.net.server.domain

import com.medic.net.server.domain.model.Account

interface AccountsRepository {
    suspend fun save(account: Account)
    suspend fun load(): List<Account>
    suspend fun remove(serverUrl: String)
}