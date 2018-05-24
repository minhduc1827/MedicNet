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


class AuthenticationActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var presenter: AuthenticationPresenter
    val job = Job()
    val TAG = AuthenticationActivity::class.java.simpleName

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
        getOrganisationList(url)
    }

    fun getOrganisationList(url: String) {
        Log.d(TAG, "getOrganisationList @url= " + url);
        /*val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            }
        })
        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.getSocketFactory()
        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        builder.hostnameVerifier(object : HostnameVerifier{
            override fun verify(hostname: String, session: SSLSession): Boolean {
                return true
            }
        })
        builder.build();*/
        val request = Request.Builder()
                .url(url)
                .build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "onFailure getOrganisationList: " + e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "onResponse getOrganisationList: " + response.body()!!.string().toString())
            }
        })
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
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