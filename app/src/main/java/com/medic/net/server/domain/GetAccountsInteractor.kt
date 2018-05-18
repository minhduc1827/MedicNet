package com.medic.net.server.domain

import javax.inject.Inject

class GetAccountsInteractor @Inject constructor(val repository: AccountsRepository) {
    suspend fun get() = repository.load()
}