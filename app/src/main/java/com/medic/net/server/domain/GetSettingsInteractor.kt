package com.medic.net.server.domain

import javax.inject.Inject

class GetSettingsInteractor @Inject constructor(private val repository: SettingsRepository) {
    fun get(url: String) = repository.get(url)
}