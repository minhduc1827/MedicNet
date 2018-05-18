package com.medic.net.pinnedmessages.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.medic.net.R
import com.medic.net.chatroom.adapter.ChatRoomAdapter
import com.medic.net.chatroom.ui.ChatRoomActivity
import com.medic.net.chatroom.viewmodel.BaseViewModel
import com.medic.net.helper.EndlessRecyclerViewScrollListener
import com.medic.net.pinnedmessages.presentation.PinnedMessagesPresenter
import com.medic.net.pinnedmessages.presentation.PinnedMessagesView
import com.medic.net.util.extensions.inflate
import com.medic.net.util.extensions.showToast
import com.medic.net.util.extensions.ui
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_pinned_messages.*
import javax.inject.Inject

fun newInstance(chatRoomId: String, chatRoomType: String): Fragment {
    return PinnedMessagesFragment().apply {
        arguments = Bundle(1).apply {
            putString(BUNDLE_CHAT_ROOM_ID, chatRoomId)
            putString(BUNDLE_CHAT_ROOM_TYPE, chatRoomType)
        }
    }
}

private const val BUNDLE_CHAT_ROOM_ID = "chat_room_id"
private const val BUNDLE_CHAT_ROOM_TYPE = "chat_room_type"

class PinnedMessagesFragment : Fragment(), PinnedMessagesView {

    private lateinit var chatRoomId: String
    private lateinit var chatRoomType: String
    private lateinit var adapter: ChatRoomAdapter
    @Inject
    lateinit var presenter: PinnedMessagesPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)

        val bundle = arguments
        if (bundle != null) {
            chatRoomId = bundle.getString(BUNDLE_CHAT_ROOM_ID)
            chatRoomType = bundle.getString(BUNDLE_CHAT_ROOM_TYPE)
        } else {
            requireNotNull(bundle) { "no arguments supplied when the fragment was instantiated" }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = container?.inflate(R.layout.fragment_pinned_messages)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        presenter.loadPinnedMessages(chatRoomId)
    }

    override fun showPinnedMessages(pinnedMessages: List<BaseViewModel<*>>) {
        ui {
            if (recycler_view_pinned.adapter == null) {
                adapter = ChatRoomAdapter(chatRoomType, "", null, false)
                recycler_view_pinned.adapter = adapter
                val linearLayoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recycler_view_pinned.layoutManager = linearLayoutManager
                recycler_view_pinned.itemAnimator = DefaultItemAnimator()
                if (pinnedMessages.size > 10) {
                    recycler_view_pinned.addOnScrollListener(object :
                        EndlessRecyclerViewScrollListener(linearLayoutManager) {
                        override fun onLoadMore(
                            page: Int,
                            totalItemsCount: Int,
                            recyclerView: RecyclerView?
                        ) {
                            presenter.loadPinnedMessages(chatRoomId)
                        }

                    })
                }
                pin_view.isVisible = pinnedMessages.isEmpty()
            }
            adapter.appendData(pinnedMessages)
        }
    }

    override fun showMessage(resId: Int) {
        ui {
            showToast(resId)
        }
    }

    override fun showMessage(message: String) {
        ui {
            showToast(message)
        }
    }

    override fun showGenericErrorMessage() = showMessage(getString(R.string.msg_generic_error))

    override fun showLoading() {
        ui { view_loading.isVisible = true }
    }

    override fun hideLoading() {
        ui { view_loading.isVisible = false }
    }

    private fun setupToolbar() {
        (activity as ChatRoomActivity).setupToolbarTitle(getString(R.string.title_pinned_messages))
    }
}