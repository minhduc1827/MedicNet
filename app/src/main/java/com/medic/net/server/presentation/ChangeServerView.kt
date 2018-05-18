package com.medic.net.server.presentation

interface ChangeServerView {
    fun showInvalidCredentials()
    fun showProgress()
    fun hideProgress()
}