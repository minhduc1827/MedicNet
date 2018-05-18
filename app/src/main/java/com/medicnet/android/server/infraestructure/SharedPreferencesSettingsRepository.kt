package com.medicnet.android.server.infraestructure

import chat.rocket.core.internal.SettingsAdapter
import com.medicnet.android.infrastructure.LocalRepository
import com.medicnet.android.infrastructure.LocalRepository.Companion.SETTINGS_KEY
import com.medicnet.android.server.domain.PublicSettings
import com.medicnet.android.server.domain.SettingsRepository

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