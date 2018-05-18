package com.medic.net.authentication.server.presentation

import com.medic.net.authentication.domain.model.LoginDeepLinkInfo
import com.medic.net.authentication.presentation.AuthenticationNavigator
import com.medic.net.core.behaviours.showMessage
import com.medic.net.core.lifecycle.CancelStrategy
import com.medic.net.server.domain.GetAccountsInteractor
import com.medic.net.server.domain.RefreshSettingsInteractor
import com.medic.net.server.domain.SaveCurrentServerInteractor
import com.medic.net.server.infraestructure.RocketChatClientFactory
import com.medic.net.server.presentation.CheckServerPresenter
import com.medic.net.util.extensions.isValidUrl
import com.medic.net.util.extensions.launchUI
import javax.inject.Inject

class ServerPresenter @Inject constructor(private val view: ServerView,
                                          private val strategy: CancelStrategy,
                                          private val navigator: AuthenticationNavigator,
                                          private val serverInteractor: SaveCurrentServerInteractor,
                                          private val refreshSettingsInteractor: RefreshSettingsInteractor,
                                          private val getAccountsInteractor: GetAccountsInteractor,
                                          factory: RocketChatClientFactory
) : CheckServerPresenter(strategy, factory, view) {

    fun checkServer(server: String) {
        if (!server.isValidUrl()) {
            view.showInvalidServerUrlMessage()
        } else {
            view.showLoading()
            checkServerInfo(server)
        }
    }

    fun connect(server: String) {
        connectToServer(server) {
            navigator.toLogin()
        }
    }

    private fun connectToServer(server: String, block: () -> Unit) {
        if (!server.isValidUrl()) {
            view.showInvalidServerUrlMessage()
        } else {
            launchUI(strategy) {
                // Check if we already have an account for this server...
                val account = getAccountsInteractor.get().firstOrNull { it.serverUrl == server }
                if (account != null) {
                    navigator.toChatList(server)
                    return@launchUI
                }

                view.showLoading()
                try {
                    refreshSettingsInteractor.refresh(server)
                    serverInteractor.save(server)
                    block()
                } catch (ex: Exception) {
                    view.showMessage(ex)
                } finally {
                    view.hideLoading()
                }
            }
        }
    }

    fun deepLink(deepLinkInfo: LoginDeepLinkInfo) {
        connectToServer(deepLinkInfo.url) {
            navigator.toLogin(deepLinkInfo)
        }
    }
}