package com.medic.net.server.domain

import javax.inject.Inject

class GetCurrentServerInteractor @Inject constructor(private val repository: CurrentServerRepository) {
    fun get(): String? = repository.get()

    fun clear() {
        repository.clear()
    }
}