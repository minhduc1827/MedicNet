package com.medic.net.server.domain

import com.medic.net.server.domain.model.Account
import javax.inject.Inject

class SaveAccountInteractor @Inject constructor(val repository: AccountsRepository) {
    suspend fun save(account: Account) = repository.save(account)
}