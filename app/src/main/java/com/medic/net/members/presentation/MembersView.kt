package com.medic.net.members.presentation

import com.medic.net.core.behaviours.LoadingView
import com.medic.net.core.behaviours.MessageView
import com.medic.net.members.viewmodel.MemberViewModel

interface MembersView: LoadingView, MessageView {

    /**
     * Shows a list of members of a room.
     *
     * @param dataSet The data set to show.
     * @param total The total number of members.
     */
    fun showMembers(dataSet: List<MemberViewModel>, total: Long)
}