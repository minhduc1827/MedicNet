package com.medic.net.helper

import com.medic.net.infrastructure.LocalRepository
import com.medic.net.server.domain.GetCurrentServerInteractor
import com.medic.net.server.domain.PublicSettings
import com.medic.net.server.domain.SettingsRepository
import com.medic.net.server.domain.useRealName
import chat.rocket.common.model.User
import javax.inject.Inject

class UserHelper @Inject constructor(
    private val localRepository: LocalRepository,
    private val getCurrentServerInteractor: GetCurrentServerInteractor,
    settingsRepository: SettingsRepository
) {

    private val settings: PublicSettings = settingsRepository.get(getCurrentServerInteractor.get()!!)

    /**
     * Return the display name for the given [user].
     * If setting 'Use_Real_Name' is true then the real name will be given, or else
     * the username without the '@' is yielded. The fallback for any case is the username, which
     * could be null.
     */
    fun displayName(user: User): String? {
        return if (settings.useRealName()) user.name ?: user.username else user.username
    }

    /**
     * Return current logged user's display name.
     *
     * @see displayName
     */
    fun displayName(): String? {
        user()?.let {
            return displayName(it)
        }
        return null
    }

    /**
     * Return current logged [User].
     */
    fun user(): User? {
        return localRepository.getCurrentUser(serverUrl())
    }

    /**
     * Return the username for the current logged [User].
     */
    fun username(): String? = localRepository.get(LocalRepository.CURRENT_USERNAME_KEY, null)

    /**
     * Whether current [User] is admin on the current server.
     */
    fun isAdmin(): Boolean {
        return user()?.roles?.find { it.equals("admin", ignoreCase = true) } != null
    }

    private fun serverUrl(): String {
        return getCurrentServerInteractor.get()!!
    }
}