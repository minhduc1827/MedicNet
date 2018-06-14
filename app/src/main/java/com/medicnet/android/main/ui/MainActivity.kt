package com.medicnet.android.main.ui

import DrawableHelper
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import chat.rocket.common.model.UserStatus
import com.google.android.gms.gcm.GoogleCloudMessaging
import com.google.android.gms.iid.InstanceID
import com.medicnet.android.BuildConfig
import com.medicnet.android.R
import com.medicnet.android.app.RocketChatApplication
import com.medicnet.android.chatroom.ui.ChatRoomFragment
import com.medicnet.android.chatrooms.ui.ChatRoomsFragment
import com.medicnet.android.contacts.model.UserItem
import com.medicnet.android.contacts.ui.NewTeamFragment
import com.medicnet.android.infrastructure.LocalRepository
import com.medicnet.android.main.adapter.AccountsAdapter
import com.medicnet.android.main.adapter.Selector
import com.medicnet.android.main.presentation.MainPresenter
import com.medicnet.android.main.presentation.MainView
import com.medicnet.android.main.viewmodel.NavHeaderViewModel
import com.medicnet.android.photo.ui.EditPhotoFragment
import com.medicnet.android.photo.ui.TakePhotoFragment
import com.medicnet.android.server.domain.model.Account
import com.medicnet.android.util.AppUtil
import com.medicnet.android.util.LogUtil
import com.medicnet.android.util.RequestUtil
import com.medicnet.android.util.extensions.*
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_chat_room.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.nav_medicnet_header.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


class MainActivity : AppCompatActivity(), MainView, HasActivityInjector, HasSupportFragmentInjector {
    @Inject lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    @Inject lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var presenter: MainPresenter
    private var isFragmentAdded: Boolean = false
    private var expanded = false
    private val headerLayout by lazy { view_navigation.getHeaderView(0) }
    private val TAG: String = MainActivity::class.java.simpleName
    private var rocketChatApplication: RocketChatApplication? = null
    private val LOCKSCREEN_REQUEST_CODE: Int = 123
    private var needShowLockScreen: Boolean = true
    var username: String? = ""
    var userList: List<UserItem>? = null
    private var teamFragment: NewTeamFragment? = null
    private var chatRoomsFragment: ChatRoomsFragment? = null
    var chatRoomFragment: ChatRoomFragment? = null
    var takePhotoFragment: TakePhotoFragment? = null
    var editPhotoFragment: EditPhotoFragment? = null
    private val handler = Handler()

    var listBitmap: MutableList<Bitmap> = ArrayList()

    companion object {
        var EXTRA_REDIRECT_TO_MAIN = "extra_redirect_to_main"
    }

    fun handleBitmap(bitmap: Bitmap, position: Int) {
        if (position > -1)
            listBitmap.set(position, bitmap)
        else //negative: add bitmap
            listBitmap.add(bitmap)
        if (takePhotoFragment != null && takePhotoFragment!!.isAdded)
            takePhotoFragment!!.updatePhotoList(listBitmap);
        if (editPhotoFragment != null && editPhotoFragment!!.isAdded)
            editPhotoFragment!!.updatePhotoList(listBitmap);
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
                /* val getCurrentServerInteractor: GetCurrentServerInteractor=GetCurrentServerInteractor()
                 val currentServer = getCurrentServerInteractor.get()
                 val serverToken = currentServer?.let { tokenRepository.get(currentServer) }*/
            } catch (ex: Exception) {
                Timber.d(ex, "Missing play services...")
            }
        }
        LogUtil.d(TAG, "@token= " + RequestUtil.token + " @userId= " + RequestUtil.userId)
        presenter.connect()
        presenter.loadCurrentInfo()
        val prefs = getSharedPreferences("rocket.chat", Context.MODE_PRIVATE)
        username = prefs?.getString(LocalRepository.CURRENT_USERNAME_KEY, "")
        LogUtil.d(TAG, "getusername from preference= " + username)
        setupToolbar()
        setupNavigationView()
        setupPassCodeScreen()
        RequestUtil.handleGetRequest(RequestUtil.GET_USER_LIST_URL, object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                LogUtil.d(TAG, "onFailure get user list")
            }

            override fun onResponse(call: Call?, response: Response?) {
                if (response?.body() != null) {
                    var json = response!!.body()!!.string().toString()
                    userList = AppUtil.getUserList(json, username)
                    LogUtil.d(TAG, "onResponse get user list>>" + json + " @size= " + (userList as java.util.ArrayList<UserItem>?)!!.size)
                }
            }

        })
        layoutAddTeam.setOnClickListener {
            teamFragment = presenter.toNewTeam()
        }
    }

    fun createNewTeam(name: String,
                      usersList: List<String>?,
                      readOnly: Boolean? = false) {
        presenter.createChannel(name, usersList, readOnly) { isSuccess ->
            if (isSuccess) {
                LogUtil.d(TAG, "Add new team successfully")
                presenter.removeFragment(teamFragment!!)
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        chatRoomsFragment!!.isGlobalLayoutListenerSetUp = false
                        handler.removeCallbacks(this)
                    }
                }, 100)

            } else {
                showMessage(R.string.msg_generic_error)
            }
        }
    }


    private fun setupPassCodeScreen() {
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


    override fun onResume() {
        super.onResume()
        if (!isFragmentAdded) {
            chatRoomsFragment = presenter.toChatList()
            isFragmentAdded = true

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
        layoutSearch.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val height: Int = layoutSearch.height
                LogUtil.d(TAG, "height @layoutsearch=" + height)
                image_avatar.layoutParams.width = height
                image_avatar.layoutParams.height = height
                viewAvatar.layoutParams.width = height + 2
                viewAvatar.layoutParams.height = height + 2
                image_avatar.requestLayout()
                imvUserStatus.requestLayout()
                layoutSearch.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
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
        /*if (showRoomTypeIcon) {
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
        }*/
    }
}