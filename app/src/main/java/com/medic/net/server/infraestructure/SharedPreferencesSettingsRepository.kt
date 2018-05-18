package com.medic.net.server.infraestructure

import com.medic.net.infrastructure.LocalRepository
import com.medic.net.infrastructure.LocalRepository.Companion.SETTINGS_KEY
import com.medic.net.server.domain.PublicSettings
import com.medic.net.server.domain.SettingsRepository
import chat.rocket.core.internal.SettingsAdapter

class SharedPreferencesSettingsRepository(
    private val localRepository: LocalRepository
) : SettingsRepository {

    private val adapter = SettingsAdapter().lenient()

    override fun save(url: String, settings: PublicSettings) {
        localRepository.save("$SETTINGS_KEY$url", adapter.toJson(settings))
    }

    override fun get(url: String): PublicSettings {
        val settings = localRepository.get("$SETTINGS_KEY$url")
        return if (settings == null) hashMapOf() else adapter.fromJson(settings) ?: hashMapOf()
    }
}