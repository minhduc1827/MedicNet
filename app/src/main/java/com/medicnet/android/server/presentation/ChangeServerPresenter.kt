package com.medicnet.android.server.presentation

import chat.rocket.common.util.ifNull
import com.medicnet.android.core.lifecycle.CancelStrategy
import com.medicnet.android.infrastructure.LocalRepository
import com.medicnet.android.server.domain.*
import com.medicnet.android.server.infraestructure.ConnectionManagerFactory
import com.medicnet.android.util.extensions.launchUI
import javax.inject.Inject

class ChangeServerPresenter @Inject constructor(
    private val view: ChangeServerView,
    private val navigator: ChangeServerNavigator,
    private val strategy: CancelStrategy,
    private val saveCurrentServerInteractor: SaveCurrentServerInteractor,
    private val getCurrentServerInteractor: GetCurrentServerInteractor,
    private val getAccountInteractor: GetAccountInteractor,
    private val getAccountsInteractor: GetAccountsInteractor,
    private val settingsRepository: SettingsRepository,
    private val tokenRepository: TokenRepository,
    private val localRepository: LocalRepository,
    private val connectionManager: ConnectionManagerFactory
) {
    fun loadServer(newUrl: String?) {
        launchUI(strategy) {
            view.showProgress()
            var url = newUrl
            if (url == null) { // Try to load next server on the list...
                val accounts = getAccountsInteractor.get()
                url = accounts.firstOrNull()?.serverUrl
            }

            url?.let { serverUrl ->
                val token = tokenRepository.get(serverUrl)
                if (token == null) {
                    view.showInvalidCredentials()
                    view.hideProgress()
                    navigator.toServerScreen()
                    return@launchUI
                }

                val settings = settingsRepository.get(serverUrl)
                if (settings == null) {
                    // TODO - reload settings...
                }

                // Call disconnect on the old url if any...
                getCurrentServerInteractor.get()?.let { url ->
                    connectionManager.get(url)?.disconnect()
                }

                // Save the current username.
                getAccountInteractor.get(serverUrl)?.let { account ->
                    localRepository.save(LocalRepository.CURRENT_USERNAME_KEY, account.userName)
                }

                saveCurrentServerInteractor.save(serverUrl)
                view.hideProgress()
                navigator.toChatRooms()
            }.ifNull {
                view.hideProgress()
                navigator.toServerScreen()
            }
        }
    }
}