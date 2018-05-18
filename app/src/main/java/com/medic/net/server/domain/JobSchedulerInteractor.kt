package com.medic.net.server.domain

interface JobSchedulerInteractor {
    /**
     * Schedule job to retry previously failed sending messages.
     */
    fun scheduleSendingMessages()
}