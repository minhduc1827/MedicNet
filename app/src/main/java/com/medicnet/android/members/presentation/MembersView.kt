package com.medicnet.android.members.presentation

import com.medicnet.android.core.behaviours.LoadingView
import com.medicnet.android.core.behaviours.MessageView
import com.medicnet.android.members.viewmodel.MemberViewModel

interface MembersView: LoadingView, MessageView {

    /**
     * Shows a list of members of a room.
     *
     * @param dataSet The data set to show.
     * @param total The total number of members.
     */
    fun showMembers(dataSet: List<MemberViewModel>, total: Long)
}