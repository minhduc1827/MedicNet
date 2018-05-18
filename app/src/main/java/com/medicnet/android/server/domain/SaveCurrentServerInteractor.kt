package com.medicnet.android.server.domain

import javax.inject.Inject

class SaveCurrentServerInteractor @Inject constructor(private val repository: CurrentServerRepository) {
    fun save(url: String) = repository.save(url)
}