package com.medicnet.android.authentication.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.medicnet.android.R
import com.medicnet.android.authentication.domain.model.LoginDeepLinkInfo
import com.medicnet.android.authentication.domain.model.getLoginDeepLinkInfo
import com.medicnet.android.authentication.login.ui.LoginFragment
import com.medicnet.android.authentication.presentation.AuthenticationPresenter
import com.medicnet.android.authentication.server.ui.ServerFragment
import com.medicnet.android.util.LogUtil
import com.medicnet.android.util.RequestUtil
import com.medicnet.android.util.extensions.addFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject


class AuthenticationActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var presenter: AuthenticationPresenter
    val job = Job()
    val TAG = AuthenticationActivity::class.java.simpleName
    var organizationJson: String = ""
    var organzation: String = ""
    var roleJson: String = ""
    var role: String = ""
    var isShowedChatList: Boolean = false
    val LOCKSCREEN_REQUEST_CODE: Int = 123


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_authentication)
        setTheme(R.style.AuthenticationTheme)
        super.onCreate(savedInstanceState)

        val deepLinkInfo = intent.getLoginDeepLinkInfo()
        launch(UI + job) {
            val newServer = intent.getBooleanExtra(INTENT_ADD_NEW_SERVER, false)
            // if we got authenticateWithDeepLink information, pass true to newServer also
            presenter.loadCredentials(newServer || deepLinkInfo != null) { authenticated ->
                if (!authenticated) {
                    showServerInput(savedInstanceState, deepLinkInfo)
                } else {
                    presenter.loadChatList(true)
                    isShowedChatList = true
//                    displayLockScreen(false)
                }
            }
        }
        RequestUtil.handleGetRequest(RequestUtil.GET_ORGANIZATION_LIST_URL, organizationCallBack())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtil.d(TAG, "onActivityResult @requestCode= " + requestCode + " @resultCode=" + resultCode + " @isShowedChatList= " + isShowedChatList)
        if (requestCode == LOCKSCREEN_REQUEST_CODE && resultCode == Activity.RESULT_OK && !isShowedChatList)
            presenter.loadChatList()
    }

    private fun organizationCallBack() = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            LogUtil.d(TAG, "onFailure getDataFromSever: " + e.message)
        }

        override fun onResponse(call: Call, response: Response) {
            organizationJson = response.body()!!.string().toString()
            LogUtil.d(TAG, "onResponse getDataFromSever: " + organizationJson)
        }
    }

    /* private fun getDataFromSever(url: String, callback: Callback) {
         LogUtil.d(TAG, "getDataFromSever @url= " + url);
         val client = AppModule.createUnsafeOkHttpClient().build()
         val handleGetRequest = Request.Builder()
                 .url(url)
                 .build()
         client.newCall(handleGetRequest).enqueue(callback)
     }*/

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment is LoginFragment) {
            LogUtil.d(TAG, "exit app onBackPressed when in login")
            finish()
        } else
            super.onBackPressed()

    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    private fun showServerInput(savedInstanceState: Bundle?, deepLinkInfo: LoginDeepLinkInfo?) {
        addFragment("ServerFragment", R.id.fragment_container) {
            ServerFragment.newInstance(deepLinkInfo)
        }
    }
}

const val INTENT_ADD_NEW_SERVER = "INTENT_ADD_NEW_SERVER"

fun Context.newServerIntent(): Intent {
    return Intent(this, AuthenticationActivity::class.java).apply {
        putExtra(INTENT_ADD_NEW_SERVER, true)
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}