package com.medic.net.members.viewmodel

import com.medic.net.server.domain.GetCurrentServerInteractor
import com.medic.net.server.domain.GetSettingsInteractor
import com.medic.net.server.domain.baseUrl
import chat.rocket.common.model.User
import chat.rocket.core.model.Value
import javax.inject.Inject

class MemberViewModelMapper @Inject constructor(serverInteractor: GetCurrentServerInteractor, getSettingsInteractor: GetSettingsInteractor) {
    private var settings: Map<String, Value<Any>> = getSettingsInteractor.get(serverInteractor.get()!!)
    private val baseUrl = settings.baseUrl()

    fun mapToViewModelList(memberList: List<User>): List<MemberViewModel> {
        return memberList.map { MemberViewModel(it, settings, baseUrl) }
    }
}