package com.medicnet.android.server.presentation

interface ChangeServerView {
    fun showInvalidCredentials()
    fun showProgress()
    fun hideProgress()
}