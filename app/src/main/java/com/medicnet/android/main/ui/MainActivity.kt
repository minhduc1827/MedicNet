package com.medicnet.android.main.ui

import DateTimeHelper
import DrawableHelper
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import chat.rocket.common.model.RoomType
import chat.rocket.common.model.UserStatus
import chat.rocket.common.model.roomTypeOf
import chat.rocket.core.model.ChatRoom
import com.google.android.gms.gcm.GoogleCloudMessaging
import com.google.android.gms.iid.InstanceID
import com.medicnet.android.BuildConfig
import com.medicnet.android.R
import com.medicnet.android.app.RocketChatApplication
import com.medicnet.android.chatrooms.ui.ChatRoomsFragment
import com.medicnet.android.helper.Constants
import com.medicnet.android.helper.SharedPreferenceHelper
import com.medicnet.android.infrastructure.LocalRepository
import com.medicnet.android.main.adapter.AccountsAdapter
import com.medicnet.android.main.adapter.Selector
import com.medicnet.android.main.presentation.MainPresenter
import com.medicnet.android.main.presentation.MainView
import com.medicnet.android.main.viewmodel.NavHeaderViewModel
import com.medicnet.android.server.domain.model.Account
import com.medicnet.android.util.AppUtil
import com.medicnet.android.util.LogUtil
import com.medicnet.android.util.extensions.*
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_chat_room.*
import kotlinx.android.synthetic.main.item_my_vault.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.nav_medicnet_header.*
import kotlinx.android.synthetic.main.unread_messages_badge_my_vault.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import javax.inject.Inject


class MainActivity : AppCompatActivity(), MainView, HasActivityInjector, HasSupportFragmentInjector {
    @Inject lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    @Inject lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var presenter: MainPresenter
    private var isFragmentAdded: Boolean = false
    private var expanded = false
    private val headerLayout by lazy { view_navigation.getHeaderView(0) }
    val TAG: String = MainActivity::class.java.simpleName
    var rocketChatApplication: RocketChatApplication? = null
    val LOCKSCREEN_REQUEST_CODE: Int = 123
    var needShowLockScreen: Boolean = true
    var username: String? = ""
    var isMyVaultClicked: Boolean = false
    var chatRoomsFragment: ChatRoomsFragment? = null

    companion object {
        var EXTRA_REDIRECT_TO_MAIN = "extra_redirect_to_main"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        launch(CommonPool) {
            try {
                val token = InstanceID.getInstance(this@MainActivity).getToken(getString(R.string.gcm_sender_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null)
                Timber.d("GCM token: $token")
                presenter.refreshToken(token)
            } catch (ex: Exception) {
                Timber.d(ex, "Missing play services...")
            }
        }

        presenter.connect()
        presenter.loadCurrentInfo()
        val prefs = getSharedPreferences("rocket.chat", Context.MODE_PRIVATE)
        username = prefs?.getString(LocalRepository.CURRENT_USERNAME_KEY, "")
        setupToolbar()
        setupNavigationView()
        setupPassCodeScreen()

        layoutMyVault.setOnClickListener {
            LogUtil.d(TAG, "onClick my vault")
            var tagChatRoom: ChatRoom? = null
            if (layoutMyVault.tag != null)
                tagChatRoom = layoutMyVault.tag as ChatRoom
            if (tagChatRoom != null) {
                drawer_layout.closeDrawer(Gravity.START)
//                val fragment = supportFragmentManager.findFragmentByTag(ChatRoomsFragment.TAG) as ChatRoomsFragment
                chatRoomsFragment?.changeItemBgColor(Color.WHITE)
                chatRoomsFragment?.loadChatRoom(tagChatRoom)
            }
        }
    }

    fun groupChatRooms() {
        val groupByType = SharedPreferenceHelper.getBoolean(Constants.CHATROOM_GROUP_BY_TYPE_KEY, false)
        if (!groupByType) {
            SharedPreferenceHelper.putBoolean(Constants.CHATROOM_GROUP_BY_TYPE_KEY, true)
            chatRoomsFragment?.presenter?.updateSortedChatRooms()
        }
    }

    fun setupPassCodeScreen() {
        val isRedirect = intent.getBooleanExtra(EXTRA_REDIRECT_TO_MAIN, true)
        LogUtil.d(TAG, "onCreate @isRedirect= " + isRedirect)
        if (isRedirect)
            AppUtil.displayLockScreen(this, false, LOCKSCREEN_REQUEST_CODE);
        if (rocketChatApplication == null) {
            rocketChatApplication = application as RocketChatApplication
            rocketChatApplication!!.appLifecycleObserver.setOnLifeCycleCallBack { isForeGround ->
                LogUtil.d(TAG, "onLifeCycle callback @isForeGround= " + isForeGround);
                if (isForeGround) {
                    if (needShowLockScreen)
                        AppUtil.displayLockScreen(this, false, LOCKSCREEN_REQUEST_CODE);
                    else
                        needShowLockScreen = true
                }
            }
        }
    }

    fun setupMyVault(chatRoom: ChatRoom?) {
        if (chatRoom != null) {
            text_last_message_my_vault.setVisible(true)
            text_last_message_date_time_my_vault.setVisible(true)
            bindLastMessageDateTime(chatRoom, text_last_message_date_time_my_vault)
            bindLastMessage(chatRoom, text_last_message_my_vault)
            bindUnreadMessages(chatRoom, text_total_unread_messages_my_vault)
            if (chatRoom.alert || chatRoom.unread > 0) {
                text_chat_name_my_vault.setTextColor(ContextCompat.getColor(this,
                        R.color.red_dark))
                text_last_message_date_time_my_vault.setTextColor(ContextCompat.getColor(this,
                        R.color.colorAccent))
                text_last_message_my_vault.setTextColor(ContextCompat.getColor(this,
                        android.R.color.primary_text_light))
            } else {
                text_chat_name_my_vault.setTextColor(ContextCompat.getColor(this,
                        R.color.red_dark))
                text_last_message_date_time_my_vault.setTextColor(ContextCompat.getColor(this,
                        R.color.colorSecondaryText))
                text_last_message_my_vault.setTextColor(ContextCompat.getColor(this,
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
            textView.content = DateTimeHelper.getDate(localDateTime, this)
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
                    val user = if (username.equals(chatRoom.name)) {
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

    override fun onResume() {
        super.onResume()
        if (!isFragmentAdded) {
            presenter.toChatList()
            isFragmentAdded = true
            fragment_container.postDelayed(Runnable {
                if (chatRoomsFragment == null)
                    chatRoomsFragment = supportFragmentManager.findFragmentByTag(ChatRoomsFragment.TAG) as ChatRoomsFragment
                groupChatRooms()
            }, 200)

        }

    }

    override fun onDestroy() {
//        unregisterReceiver(mScreenStateReceiver)
        super.onDestroy()
        if (isFinishing) {
            presenter.disconnect()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtil.d(TAG, "onActivityResult @requestCode= " + requestCode + " @resultCode=" + resultCode)
        if (requestCode == LOCKSCREEN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //passcode callback here
        }
    }

    override fun showUserStatus(userStatus: UserStatus) {
        headerLayout.apply {
            image_user_status.setImageDrawable(
                DrawableHelper.getUserStatusDrawable(userStatus, this.context)
            )
        }
    }

    override fun setupNavHeader(viewModel: NavHeaderViewModel, accounts: List<Account>) {
        Timber.d("Setting up nav header: $viewModel")
        /*with(headerLayout) {
            with(viewModel) {
                if (userStatus != null) {
                    image_user_status.setImageDrawable(
                        DrawableHelper.getUserStatusDrawable(userStatus, context)
                    )
                }
                if (userDisplayName != null) {
                    text_user_name.text = userDisplayName
                }
                if (userAvatar != null) {
                    image_avatar.setImageURI(userAvatar)
                }
                if (serverLogo != null) {
                    server_logo.setImageURI(serverLogo)
                }
                text_server_url.text = viewModel.serverUrl
            }
            setupAccountsList(headerLayout, accounts)
        }*/
        if (viewModel.userAvatar != null) {
            LogUtil.d(TAG, "setupNavHeader @userAvatar= " + viewModel.userAvatar)
            image_avatar.setImageURI(viewModel.userAvatar)
        }


        if (viewModel.userStatus != null) {
            LogUtil.d(TAG, "setupNavHeader @userStatus= " + viewModel.userStatus)
            imvUserStatus.setImageDrawable(
                    DrawableHelper.getUserStatusDrawable(viewModel.userStatus, this)
            )
        }
    }

    override fun closeServerSelection() {
        view_navigation.getHeaderView(0).account_container.performClick()
    }

    override fun alertNotRecommendedVersion() {
        AlertDialog.Builder(this)
                .setMessage(getString(R.string.msg_ver_not_recommended, BuildConfig.RECOMMENDED_SERVER_VERSION))
                .setPositiveButton(R.string.msg_ok, null)
                .create()
                .show()
    }

    override fun blockAndAlertNotRequiredVersion() {
        AlertDialog.Builder(this)
                .setMessage(getString(R.string.msg_ver_not_minimum, BuildConfig.REQUIRED_SERVER_VERSION))
                .setOnDismissListener { presenter.logout() }
                .setPositiveButton(R.string.msg_ok, null)
                .create()
                .show()
    }

    private fun setupAccountsList(header: View, accounts: List<Account>) {
        accounts_list.layoutManager = LinearLayoutManager(this)
        accounts_list.adapter = AccountsAdapter(accounts, object : Selector {
            override fun onStatusSelected(userStatus: UserStatus) {
                presenter.changeDefaultStatus(userStatus)
            }

            override fun onAccountSelected(serverUrl: String) {
                presenter.changeServer(serverUrl)
            }

            override fun onAddedAccountSelected() {
                presenter.addNewServer()
            }
        })

        header.account_container.setOnClickListener {
            header.image_account_expand.rotateBy(180f)
            if (expanded) {
                accounts_list.fadeOut()
            } else {
                accounts_list.fadeIn()
            }

            expanded = !expanded
        }

        header.image_avatar.setOnClickListener {
            view_navigation.menu.findItem(R.id.action_profile).isChecked = true
            presenter.toUserProfile()
            drawer_layout.closeDrawer(Gravity.START)
        }
    }

    override fun showMessage(resId: Int) = showToast(resId)

    override fun showMessage(message: String) = showToast(message)

    override fun showGenericErrorMessage() = showMessage(getString(R.string.msg_generic_error))

    override fun activityInjector(): AndroidInjector<Activity> = activityDispatchingAndroidInjector

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentDispatchingAndroidInjector

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp)
        toolbar.setNavigationOnClickListener {
            drawer_layout.openDrawer(Gravity.START)
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
    }

    private fun setupNavigationView() {
        view_navigation.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawer_layout.closeDrawer(Gravity.START)
            onNavDrawerItemSelected(menuItem)
            true
        }
        layoutSearch.viewTreeObserver.addOnGlobalLayoutListener {
            val height: Int = layoutSearch.height
            LogUtil.d(TAG, "height @layoutsearch=" + height)
            image_avatar.layoutParams.width = height
            image_avatar.layoutParams.height = height
            viewAvatar.layoutParams.width = height + 2
            viewAvatar.layoutParams.height = height + 2
            image_avatar.requestLayout()
            imvUserStatus.requestLayout()

        }
    }

    private fun onNavDrawerItemSelected(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.action_chat_rooms -> {
                presenter.toChatList()
            }
            R.id.action_profile -> {
                presenter.toUserProfile()
            }
            R.id.action_settings -> {
                presenter.toSettings()
            }
            R.id.action_logout -> {
                presenter.logout()
            }
        }
    }

    fun setupToolbarTitle(toolbarTitle: String) {
        text_room_name.textContent = toolbarTitle
    }

    fun showRoomTypeIcon(showRoomTypeIcon: Boolean, chatRoomType: String) {
        if (showRoomTypeIcon) {
            val roomType = roomTypeOf(chatRoomType)
            val drawable = when (roomType) {
                is RoomType.Channel -> {
                    DrawableHelper.getDrawableFromId(R.drawable.ic_room_channel, this)
                }
                is RoomType.PrivateGroup -> {
                    DrawableHelper.getDrawableFromId(R.drawable.ic_room_lock, this)
                }
                is RoomType.DirectMessage -> {
                    DrawableHelper.getDrawableFromId(R.drawable.ic_room_dm, this)
                }
                else -> null
            }

            drawable?.let {
                val wrappedDrawable = DrawableHelper.wrapDrawable(it)
                val mutableDrawable = wrappedDrawable.mutate()
                DrawableHelper.tintDrawable(mutableDrawable, this, R.color.white)
                DrawableHelper.compoundDrawable(text_room_name, mutableDrawable)
            }
        } else {
            text_room_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }
}