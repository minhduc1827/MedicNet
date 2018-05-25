package com.medicnet.android.authentication.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.medicnet.android.R
import com.medicnet.android.authentication.domain.model.LoginDeepLinkInfo
import com.medicnet.android.authentication.domain.model.getLoginDeepLinkInfo
import com.medicnet.android.authentication.login.ui.LoginFragment
import com.medicnet.android.authentication.presentation.AuthenticationPresenter
import com.medicnet.android.authentication.server.ui.ServerFragment
import com.medicnet.android.util.extensions.addFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import okhttp3.*
import java.io.IOException
import javax.inject.Inject
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException


class AuthenticationActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var presenter: AuthenticationPresenter
    val job = Job()
    val TAG = AuthenticationActivity::class.java.simpleName
    var organizationJson: String = ""
    var organzation: String = ""

    companion object {
        //DucNM: adding unsafeOkHttp
        fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
            try {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }

                    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                        return arrayOf()
                    }
                })

                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory

                val builder = OkHttpClient.Builder()
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier { _, _ -> true }

                return builder
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

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
                }
            }
        }
        val url = getString(R.string.default_protocol) + getString(R.string.default_server) + getString(R.string.organization_list_api)
        getDataFromSever(url, organizationCallBack())
    }

    fun organizationCallBack() = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.d(TAG, "onFailure getDataFromSever: " + e.message)
        }

        override fun onResponse(call: Call, response: Response) {
            organizationJson = response.body()!!.string().toString()
            Log.d(TAG, "onResponse getDataFromSever: " + organizationJson)
        }
    }

    fun getDataFromSever(url: String, callback: Callback) {

        Log.d(TAG, "getDataFromSever @url= " + url);
        val client = getUnsafeOkHttpClient().build()
        val request = Request.Builder()
                .url(url)
                .build()
        client.newCall(request).enqueue(callback)
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment is LoginFragment) {
            Log.d(TAG, "exit app onBackPressed when in login")
            finish()
        } else
            super.onBackPressed()

    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    fun showServerInput(savedInstanceState: Bundle?, deepLinkInfo: LoginDeepLinkInfo?) {
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