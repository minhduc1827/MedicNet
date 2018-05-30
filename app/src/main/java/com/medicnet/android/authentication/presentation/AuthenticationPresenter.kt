package com.medicnet.android.authentication.presentation

import com.medicnet.android.infrastructure.LocalRepository
import com.medicnet.android.server.domain.GetAccountInteractor
import com.medicnet.android.server.domain.GetCurrentServerInteractor
import com.medicnet.android.server.domain.SettingsRepository
import com.medicnet.android.server.domain.TokenRepository
import com.medicnet.android.util.LogUtil
import javax.inject.Inject

class AuthenticationPresenter @Inject constructor(
        private val navigator: AuthenticationNavigator,
        private val getCurrentServerInteractor: GetCurrentServerInteractor,
        private val getAccountInteractor: GetAccountInteractor,
        private val settingsRepository: SettingsRepository,
        private val localRepository: LocalRepository,
        private val tokenRepository: TokenRepository
) {
    suspend fun loadCredentials(newServer: Boolean, callback: (authenticated: Boolean) -> Unit) {
        LogUtil.d("AuthenticationPresenter", "loadCredentials")
        val currentServer = getCurrentServerInteractor.get()
        val serverToken = currentServer?.let { tokenRepository.get(currentServer) }
        val settings = currentServer?.let { settingsRepository.get(currentServer) }
        val account = currentServer?.let { getAccountInteractor.get(currentServer) }

        account?.let {
            localRepository.save(LocalRepository.CURRENT_USERNAME_KEY, account.userName)
        }

        if (newServer || currentServer == null || serverToken == null || settings == null || account?.userName == null) {
            callback(false)
        } else {
//            navigator.toChatList()
            callback(true)
        }
    }

    fun loadChatList() {
        loadChatList(false)
    }

    fun loadChatList(isRedirect: Boolean) {
        navigator.toChatList(isRedirect)
    }
}