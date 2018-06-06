package com.medicnet.android.chatrooms.ui

import DateTimeHelper
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.CheckBox
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.view.isVisible
import chat.rocket.common.model.RoomType
import chat.rocket.core.internal.realtime.socket.model.State
import chat.rocket.core.model.ChatRoom
import com.medicnet.android.R
import com.medicnet.android.chatrooms.presentation.ChatRoomsPresenter
import com.medicnet.android.chatrooms.presentation.ChatRoomsView
import com.medicnet.android.helper.ChatRoomsSortOrder
import com.medicnet.android.helper.Constants
import com.medicnet.android.helper.SharedPreferenceHelper
import com.medicnet.android.infrastructure.LocalRepository
import com.medicnet.android.main.ui.MainActivity
import com.medicnet.android.server.domain.GetCurrentServerInteractor
import com.medicnet.android.server.domain.SettingsRepository
import com.medicnet.android.util.LogUtil
import com.medicnet.android.util.extensions.*
import com.medicnet.android.widget.DividerItemDecoration
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_chat_rooms.*
import kotlinx.android.synthetic.main.item_my_vault.*
import kotlinx.android.synthetic.main.unread_messages_badge_my_vault.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.NonCancellable.isActive
import timber.log.Timber
import javax.inject.Inject

class ChatRoomsFragment : Fragment(), ChatRoomsView {
    @Inject
    lateinit var presenter: ChatRoomsPresenter
    @Inject
    lateinit var serverInteractor: GetCurrentServerInteractor
    @Inject
    lateinit var settingsRepository: SettingsRepository
    @Inject
    lateinit var localRepository: LocalRepository
    private var searchView: SearchView? = null
    private val handler = Handler()

    private var listJob: Job? = null
    private var sectionedAdapter: SimpleSectionedRecyclerViewAdapter? = null
    val TAG: String = ChatRoomsFragment::class.java.simpleName
    var itemRecyclerView: View? = null
    //    var isMyVaultClicked: Boolean = false
    var mainActivity: MainActivity? = null
//    var sortByActivity: Boolean = false
var chatRoomSelected: ChatRoom? = null

    companion object {
        val TAG: String = "ChatRoomsFragment"
        fun newInstance() = ChatRoomsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
    }

    override fun onDestroy() {
        handler.removeCallbacks(dismissStatus)
        presenter.disconnect()
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = container?.inflate(R.layout.fragment_chat_rooms)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        presenter.loadChatRooms()
        if (!SharedPreferenceHelper.getBoolean(Constants.CHATROOM_GROUP_BY_TYPE_KEY, false)) {
//            sectionedAdapter?.clearSections()
//            return
            SharedPreferenceHelper.putBoolean(Constants.CHATROOM_GROUP_BY_TYPE_KEY, true)
            SharedPreferenceHelper.putInt(Constants.CHATROOM_SORT_TYPE_KEY, ChatRoomsSortOrder.ACTIVITY)
        }
        mainActivity = activity as MainActivity
        layoutMyVault.setOnClickListener {
            LogUtil.d(TAG, "onClick my vault")
            var tagChatRoom: ChatRoom? = null
            if (layoutMyVault.tag != null)
                tagChatRoom = layoutMyVault.tag as ChatRoom
            if (tagChatRoom != null) {
                mainActivity!!.drawer_layout.closeDrawer(Gravity.START)
//                val fragment = supportFragmentManager.findFragmentByTag(ChatRoomsFragment.TAG) as ChatRoomsFragment
                changeItemBgColor(Color.WHITE)
                loadChatRoom(tagChatRoom)
            }
        }
    }

    override fun onDestroyView() {
        listJob?.cancel()
        super.onDestroyView()
    }

    fun setupMyVault(chatRoom: ChatRoom?) {
        if (chatRoom != null) {
            text_last_message_my_vault.setVisible(true)
            text_last_message_date_time_my_vault.setVisible(true)
            bindLastMessageDateTime(chatRoom, text_last_message_date_time_my_vault)
            bindLastMessage(chatRoom, text_last_message_my_vault)
            bindUnreadMessages(chatRoom, text_total_unread_messages_my_vault)
            if (chatRoom.alert || chatRoom.unread > 0) {
                text_chat_name_my_vault.setTextColor(ContextCompat.getColor(activity!!,
                        R.color.red_dark))
                text_last_message_date_time_my_vault.setTextColor(ContextCompat.getColor(activity!!,
                        R.color.colorAccent))
                text_last_message_my_vault.setTextColor(ContextCompat.getColor(activity!!,
                        android.R.color.primary_text_light))
            } else {
                text_chat_name_my_vault.setTextColor(ContextCompat.getColor(activity!!,
                        R.color.red_dark))
                text_last_message_date_time_my_vault.setTextColor(ContextCompat.getColor(activity!!,
                        R.color.colorSecondaryText))
                text_last_message_my_vault.setTextColor(ContextCompat.getColor(activity!!,
                        R.color.colorSecondaryText))
            }
            /*if (!isMyVaultClicked) {
                layoutMyVault.performClick()
                isMyVaultClicked = true
            }*/
        }
    }

    private fun bindLastMessageDateTime(chatRoom: ChatRoom, textView: TextView) {
        val lastMessage = chatRoom.lastMessage
        if (lastMessage != null) {
            val localDateTime = DateTimeHelper.getLocalDateTime(lastMessage.timestamp)
            textView.content = DateTimeHelper.getDate(localDateTime, activity!!)
        } else {
            textView.content = ""
        }
    }

    private fun bindLastMessage(chatRoom: ChatRoom, textView: TextView) {
        val lastMessage = chatRoom.lastMessage
        val lastMessageSender = lastMessage?.sender
        if (lastMessage != null && lastMessageSender != null) {
            val message = lastMessage.message
            val senderUsername = lastMessageSender.name ?: lastMessageSender.username
            when (senderUsername) {
                chatRoom.name -> {
                    textView.content = message
                }
                else -> {
                    /*val user = if (localRepository.checkIfMyself(lastMessageSender.username!!)) {
                        "${this.getString(R.string.msg_you)}: "
                    } else {
                        "$senderUsername: "
                    }*/
                    val user = if (mainActivity!!.username.equals(chatRoom.name)) {
                        "${this.getString(R.string.msg_you)}: "
                    } else {
                        "$senderUsername: "
                    }
                    val spannable = SpannableStringBuilder(user)
                    val len = spannable.length
                    spannable.setSpan(ForegroundColorSpan(Color.BLACK), 0, len - 1, 0)
                    spannable.append(message)
                    textView.content = spannable
                }
            }
        } else {
//            textView.content = getText(R.string.msg_no_messages_yet)
        }
    }

    private fun bindUnreadMessages(chatRoom: ChatRoom, textView: TextView) {
        val totalUnreadMessage = chatRoom.unread
        when {
            totalUnreadMessage in 1..99 -> {
                textView.textContent = totalUnreadMessage.toString()
                textView.setVisible(true)
            }
            totalUnreadMessage > 99 -> {
                textView.textContent = getString(R.string.msg_more_than_ninety_nine_unread_messages)
                textView.setVisible(true)
            }
            else -> textView.setVisible(false)
        }
    }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
         inflater.inflate(R.menu.chatrooms, menu)

         val searchItem = menu.findItem(R.id.action_search)
         searchView = searchItem?.actionView as SearchView
         searchView?.maxWidth = Integer.MAX_VALUE
         searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
             override fun onQueryTextSubmit(query: String?): Boolean {
                 return queryChatRoomsByName(query)
             }

             override fun onQueryTextChange(newText: String?): Boolean {
                 return queryChatRoomsByName(newText)
             }
         })
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort -> {
                val dialogLayout = layoutInflater.inflate(R.layout.chatroom_sort_dialog, null)
                val sortType = SharedPreferenceHelper.getInt(Constants.CHATROOM_SORT_TYPE_KEY, ChatRoomsSortOrder.ACTIVITY)
                val groupByType = SharedPreferenceHelper.getBoolean(Constants.CHATROOM_GROUP_BY_TYPE_KEY, false)

                val radioGroup = dialogLayout.findViewById<RadioGroup>(R.id.radio_group_sort)
                val groupByTypeCheckBox = dialogLayout.findViewById<CheckBox>(R.id.checkbox_group_by_type)

                radioGroup.check(when (sortType) {
                    0 -> R.id.radio_sort_alphabetical
                    else -> R.id.radio_sort_activity
                })
                radioGroup.setOnCheckedChangeListener({ _, checkedId ->
                    run {
                        SharedPreferenceHelper.putInt(Constants.CHATROOM_SORT_TYPE_KEY, when (checkedId) {
                            R.id.radio_sort_alphabetical -> 0
                            R.id.radio_sort_activity -> 1
                            else -> 1
                        })
                        presenter.updateSortedChatRooms()
                        invalidateQueryOnSearch()
                    }
                })

                groupByTypeCheckBox.isChecked = groupByType
                groupByTypeCheckBox.setOnCheckedChangeListener({ _, isChecked ->
                    SharedPreferenceHelper.putBoolean(Constants.CHATROOM_GROUP_BY_TYPE_KEY, isChecked)
                    presenter.updateSortedChatRooms()
                    invalidateQueryOnSearch()
                })

                val dialogSort = AlertDialog.Builder(context)
                    .setTitle(R.string.dialog_sort_title)
                    .setView(dialogLayout)
                    .setPositiveButton("Done", { dialog, _ -> dialog.dismiss() })

                dialogSort.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun invalidateQueryOnSearch() {
        searchView?.let {
            if (!searchView!!.isIconified) {
                queryChatRoomsByName(searchView!!.query.toString())
            }
        }
    }

    override suspend fun updateChatRooms(newDataSet: List<ChatRoom>) {
        listJob?.cancel()
        listJob = ui {
            //            LogUtil.d(TAG, "updateChatRooms @newDataSet= " + newDataSet.toString())
            val dataSet: MutableList<ChatRoom> = ArrayList();
            LogUtil.d(TAG, "updateChatRooms>>")
            for (chatRoom in newDataSet) {
//                LogUtil.d(TAG, "updateChatRooms>>" + chatRoom.toString())
                if (chatRoomSelected == null || (chatRoomSelected != null && chatRoomSelected!!.lastSeen!! < chatRoom?.lastSeen!!))
                    chatRoomSelected = chatRoom
                if (mainActivity!!.username.equals(chatRoom.name)) {
//                    LogUtil.d(TAG, "updateChatRooms has myVault @chatroom= " + chatRoom.toString())
                    layoutMyVault.tag = chatRoom
                    setupMyVault(chatRoom)
                } else {
                    /*val type = chatRoom.type.toString()
                    if (type.equals(RoomType.CHANNEL.toString()))
                        chatRoom.type = RoomType.PRIVATE_GROUP*/
                    dataSet.add(chatRoom)
                }
            }
            val adapter = recycler_view.adapter as SimpleSectionedRecyclerViewAdapter
            // FIXME https://fabric.io/rocketchat3/android/apps/com.medicnet.android/issues/5ac2916c36c7b235275ccccf
            // TODO - fix this bug to re-enable DiffUtil
            /*val diff = async(CommonPool) {
                DiffUtil.calculateDiff(RoomsDiffCallback(adapter.baseAdapter.dataSet, newDataSet))
            }.await()*/
//            text_no_search.isVisible = newDataSet.isEmpty()
            text_no_search.isVisible = dataSet.isEmpty()
            if (isActive) {
//                adapter.baseAdapter.updateRooms(newDataSet)
                adapter.baseAdapter.updateRooms(dataSet)
                // TODO - fix crash to re-enable diff.dispatchUpdatesTo(adapter)
                adapter.notifyDataSetChanged()

                //Set sections always after data set is updated
                /*if (!sortByActivity) {
                    sortByActivity = true
                    presenter.updateSortedChatRooms()
                } else*/
                setSections()
            }

        }
    }

    private fun setSections() {
        LogUtil.d(TAG, "setSections")
        //Don't add section if not grouping by RoomType
        val sections = ArrayList<SimpleSectionedRecyclerViewAdapter.Section>()
        sectionedAdapter?.baseAdapter?.dataSet?.let {
            var previousChatRoomType = ""

            for ((position, chatRoom) in it.withIndex()) {
                val type = chatRoom.type.toString()
                if (/*!type.equals(RoomType.PRIVATE_GROUP.toString()) && */type != previousChatRoomType) {
                    val title = when (type) {
                        RoomType.CHANNEL.toString() -> resources.getString(R.string.label_team_chat_group)
                        RoomType.PRIVATE_GROUP.toString() -> resources.getString(R.string.label_team_chat_group)
                        RoomType.DIRECT_MESSAGE.toString() -> resources.getString(R.string.label_direct_chat_group)
                        RoomType.LIVECHAT.toString() -> resources.getString(R.string.header_live_chats)
                        else -> resources.getString(R.string.header_unknown)
                    }
                    sections.add(SimpleSectionedRecyclerViewAdapter.Section(position, title))
                }
                previousChatRoomType = chatRoom.type.toString()
            }
        }
        LogUtil.d(TAG, "setSections @sectionSize= " + sections.size)
        val dummy = arrayOfNulls<SimpleSectionedRecyclerViewAdapter.Section>(sections.size)
        sectionedAdapter?.setSections(sections.toArray(dummy))
    }

    private fun setupRecyclerView() {
        ui {
            recycler_view.layoutManager = LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false)
            recycler_view.addItemDecoration(DividerItemDecoration(it,
                    resources.getDimensionPixelSize(R.dimen.divider_item_decorator_bound_start),
                    resources.getDimensionPixelSize(R.dimen.divider_item_decorator_bound_end)))
            recycler_view.itemAnimator = DefaultItemAnimator()
            // TODO - use a ViewModel Mapper instead of using settings on the adapter

            val baseAdapter = ChatRoomsAdapter(it,
                    settingsRepository.get(serverInteractor.get()!!), localRepository) { chatRoom ->
                //                itemRecyclerView = recycler_view.getChildAt(0)
//                changeItemBgColor(ContextCompat.getColor(this!!.activity!!, R.color.dark_gray))
                LogUtil.d("ChatroomsFragment", "onItem chat clicked")
                (activity as MainActivity).drawer_layout.closeDrawer(Gravity.START)
                chatRoomSelected = chatRoom
                loadChatRoom(chatRoom)
                /*sectionedAdapter?.clearSections()
                sortByActivity = false
                presenter.loadChatRooms()*/
            }
            recycler_view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    LogUtil.d(TAG, "recycle load completely and now loadchatRoom selected>>" + chatRoomSelected.toString())
                    loadChatRoom(chatRoomSelected!!)
                    recycler_view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })


            sectionedAdapter = SimpleSectionedRecyclerViewAdapter(it,
                    R.layout.item_chatroom_header, R.id.text_chatroom_header, baseAdapter)
            recycler_view.adapter = sectionedAdapter
        }

    }


    override fun showNoChatRoomsToDisplay() {
        ui { text_no_data_to_display.setVisible(true) }
    }

    override fun showLoading() {
        ui { view_loading.setVisible(true) }
    }

    override fun hideLoading() {
        ui {
            view_loading.setVisible(false)
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

    override fun showConnectionState(state: State) {
        Timber.d("Got new state: $state")
        ui {
            connection_status_text.fadeIn()
            handler.removeCallbacks(dismissStatus)
            when (state) {
                is State.Connected -> {
                    connection_status_text.text = getString(R.string.status_connected)
                    handler.postDelayed(dismissStatus, 2000)
                }
                is State.Disconnected -> connection_status_text.text = getString(R.string.status_disconnected)
                is State.Connecting -> connection_status_text.text = getString(R.string.status_connecting)
                is State.Authenticating -> connection_status_text.text = getString(R.string.status_authenticating)
                is State.Disconnecting -> connection_status_text.text = getString(R.string.status_disconnecting)
                is State.Waiting -> connection_status_text.text = getString(R.string.status_waiting, state.seconds)
            }
        }
    }

    private val dismissStatus = {
        if (connection_status_text != null) {
            connection_status_text.fadeOut()
        }
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity?)?.supportActionBar?.title = ""
    }

    fun changeItemBgColor(color: Int) {
        if (itemRecyclerView != null)
            itemRecyclerView?.setBackgroundColor(color)
    }

    private fun loadChatRoom(chatRoom: ChatRoom) {
        presenter.loadChatRoom(chatRoom)
    }

    private fun queryChatRoomsByName(name: String?): Boolean {
        presenter.chatRoomsByName(name ?: "")
        return true
    }

    class RoomsDiffCallback(private val oldRooms: List<ChatRoom>,
                            private val newRooms: List<ChatRoom>) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldRooms[oldItemPosition].id == newRooms[newItemPosition].id
        }

        override fun getOldListSize(): Int {
            return oldRooms.size
        }

        override fun getNewListSize(): Int {
            return newRooms.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldRooms[oldItemPosition].updatedAt == newRooms[newItemPosition].updatedAt
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return newRooms[newItemPosition]
        }
    }
}