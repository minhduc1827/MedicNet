package com.medic.net.main.presentation

import com.medic.net.authentication.server.presentation.VersionCheckView
import com.medic.net.core.behaviours.MessageView
import com.medic.net.main.viewmodel.NavHeaderViewModel
import com.medic.net.server.domain.model.Account
import chat.rocket.common.model.UserStatus

interface MainView : MessageView, VersionCheckView {

    /**
     * Shows the current user status.
     *
     * @see [UserStatus]
     */
    fun showUserStatus(userStatus: UserStatus)

    /**
     * Setups the navigation header.
     *
     * @param viewModel The [NavHeaderViewModel].
     * @param accounts The list of accounts.
     */
    fun setupNavHeader(viewModel: NavHeaderViewModel, accounts: List<Account>)

    fun closeServerSelection()
}