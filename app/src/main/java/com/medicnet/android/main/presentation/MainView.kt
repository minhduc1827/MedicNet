package com.medicnet.android.main.presentation

import chat.rocket.common.model.UserStatus
import com.medicnet.android.authentication.server.presentation.VersionCheckView
import com.medicnet.android.core.behaviours.MessageView
import com.medicnet.android.main.viewmodel.NavHeaderViewModel
import com.medicnet.android.server.domain.model.Account

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