package com.medicnet.android.authentication.login.presentation

import chat.rocket.common.RocketChatAuthException
import chat.rocket.common.RocketChatException
import chat.rocket.common.RocketChatTwoFactorException
import chat.rocket.common.model.Token
import chat.rocket.common.util.ifNull
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.rest.*
import com.medicnet.android.authentication.domain.model.LoginDeepLinkInfo
import com.medicnet.android.authentication.presentation.AuthenticationNavigator
import com.medicnet.android.core.lifecycle.CancelStrategy
import com.medicnet.android.helper.OauthHelper
import com.medicnet.android.infrastructure.LocalRepository
import com.medicnet.android.server.domain.*
import com.medicnet.android.server.domain.model.Account
import com.medicnet.android.server.infraestructure.RocketChatClientFactory
import com.medicnet.android.util.extensions.*
import com.medicnet.android.util.retryIO
import kotlinx.coroutines.experimental.delay
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TYPE_LOGIN_USER_EMAIL = 0
private const val TYPE_LOGIN_CAS = 1
private const val TYPE_LOGIN_OAUTH = 2
private const val TYPE_LOGIN_DEEP_LINK = 3
private const val SERVICE_NAME_FACEBOOK = "facebook"
private const val SERVICE_NAME_GITHUB = "github"
private const val SERVICE_NAME_GOOGLE = "google"
private const val SERVICE_NAME_LINKEDIN = "linkedin"
private const val SERVICE_NAME_GILAB = "gitlab"

class LoginPresenter @Inject constructor(
    private val view: LoginView,
    private val strategy: CancelStrategy,
    private val navigator: AuthenticationNavigator,
    private val tokenRepository: TokenRepository,
    private val localRepository: LocalRepository,
    private val getAccountsInteractor: GetAccountsInteractor,
    private val settingsInteractor: GetSettingsInteractor,
    serverInteractor: GetCurrentServerInteractor,
    private val saveAccountInteractor: SaveAccountInteractor,
    private val factory: RocketChatClientFactory
) {
    // TODO - we should validate the current server when opening the app, and have a nonnull get()
    private val currentServer = serverInteractor.get()!!
    private lateinit var client: RocketChatClient
    private lateinit var settings: PublicSettings
    private lateinit var usernameOrEmail: String
    private lateinit var password: String
    private lateinit var credentialToken: String
    private lateinit var credentialSecret: String
    private lateinit var deepLinkUserId: String
    private lateinit var deepLinkToken: String

    fun setupView() {
        setupConnectionInfo(currentServer)
        setupLoginView()
        setupUserRegistrationView()
        setupForgotPasswordView()
        setupCasView()
        setupOauthServicesView()
    }

    fun authenticateWithUserAndPassword(usernameOrEmail: String, password: String, callback: (authenticated: Boolean) -> Unit) {
        when {
            usernameOrEmail.isBlank() -> {
                view.alertWrongUsernameOrEmail()
            }
            password.isEmpty() -> {
                view.alertWrongPassword()
            }
            else -> {
                this.usernameOrEmail = usernameOrEmail
                this.password = password
                doAuthentication(TYPE_LOGIN_USER_EMAIL, callback)
            }
        }
    }

    fun authenticateWithCas(token: String) {
        credentialToken = token
        doAuthentication(TYPE_LOGIN_CAS)
    }

    fun authenticateWithOauth(token: String, secret: String) {
        credentialToken = token
        credentialSecret = secret
        doAuthentication(TYPE_LOGIN_OAUTH)
    }

    fun authenticateWithDeepLink(deepLinkInfo: LoginDeepLinkInfo) {
        val serverUrl = deepLinkInfo.url
        setupConnectionInfo(serverUrl)
        deepLinkUserId = deepLinkInfo.userId
        deepLinkToken = deepLinkInfo.token
        tokenRepository.save(serverUrl, Token(deepLinkUserId, deepLinkToken))

        doAuthentication(TYPE_LOGIN_DEEP_LINK)
    }

    private fun setupConnectionInfo(serverUrl: String) {
        client = factory.create(serverUrl)
        settings = settingsInteractor.get(serverUrl)
    }

    fun signup() = navigator.toSignUp()

    fun forgotPassword() = navigator.toForgotPassword()

    private fun setupLoginView() {
        if (settings.isLoginFormEnabled()) {
            view.showFormView()
            view.setupLoginButtonListener()
            view.setupGlobalListener()
        } else {
            view.hideFormView()
        }
    }

    private fun setupCasView() {
        if (settings.isCasAuthenticationEnabled()) {
            val token = generateRandomString(17)
            view.setupCasButtonListener(settings.casLoginUrl().casUrl(currentServer, token), token)
            view.showCasButton()
        }
    }

    private fun setupUserRegistrationView() {
        if (settings.isRegistrationEnabledForNewUsers() && settings.isLoginFormEnabled()) {
            view.setupSignUpView()
            view.showSignUpView()
        }
    }

    private fun setupForgotPasswordView() {
        if (settings.isPasswordResetEnabled()) {
            view.setupForgotPasswordView()
            view.showForgotPasswordView()
        }
    }

    private fun setupOauthServicesView() {
        launchUI(strategy) {
            try {
                val services = retryIO("settingsOauth()") {
                    client.settingsOauth().services
                }
                if (services.isNotEmpty()) {
                    val state = "{\"loginStyle\":\"popup\",\"credentialToken\":\"${generateRandomString(40)}\",\"isCordova\":true}".encodeToBase64()
                    var totalSocialAccountsEnabled = 0

                    if (settings.isFacebookAuthenticationEnabled()) {
                        val clientId = getOauthClientId(services, SERVICE_NAME_FACEBOOK)
                        if (clientId != null) {
                            view.setupFacebookButtonListener(OauthHelper.getFacebookOauthUrl(clientId, currentServer, state), state)
                            view.enableLoginByFacebook()
                            totalSocialAccountsEnabled++
                        }
                    }
                    if (settings.isGithubAuthenticationEnabled()) {
                        val clientId = getOauthClientId(services, SERVICE_NAME_GITHUB)
                        if (clientId != null) {
                            view.setupGithubButtonListener(OauthHelper.getGithubOauthUrl(clientId, state), state)
                            view.enableLoginByGithub()
                            totalSocialAccountsEnabled++
                        }
                    }
                    if (settings.isGoogleAuthenticationEnabled()) {
                        val clientId = getOauthClientId(services, SERVICE_NAME_GOOGLE)
                        if (clientId != null) {
                            view.setupGoogleButtonListener(OauthHelper.getGoogleOauthUrl(clientId, currentServer, state), state)
                            view.enableLoginByGoogle()
                            totalSocialAccountsEnabled++
                        }
                    }
                    if (settings.isLinkedinAuthenticationEnabled()) {
                        val clientId = getOauthClientId(services, SERVICE_NAME_LINKEDIN)
                        if (clientId != null) {
                            view.setupLinkedinButtonListener(OauthHelper.getLinkedinOauthUrl(clientId, currentServer, state), state)
                            view.enableLoginByLinkedin()
                            totalSocialAccountsEnabled++
                        }
                    }
                    if (settings.isMeteorAuthenticationEnabled()) {
                        //TODO: Remove until we have this implemented
//                        view.enableLoginByMeteor()
//                        totalSocialAccountsEnabled++
                    }
                    if (settings.isTwitterAuthenticationEnabled()) {
                        //TODO: Remove until we have this implemented
//                        view.enableLoginByTwitter()
//                        totalSocialAccountsEnabled++
                    }
                    if (settings.isGitlabAuthenticationEnabled()) {
                        val clientId = getOauthClientId(services, SERVICE_NAME_GILAB)
                        if (clientId != null) {
                            val gitlabOauthUrl = if (settings.gitlabUrl() != null) {
                                OauthHelper.getGitlabOauthUrl(
                                    host = settings.gitlabUrl(),
                                    clientId = clientId,
                                    serverUrl = currentServer,
                                    state = state
                                )
                            } else {
                                OauthHelper.getGitlabOauthUrl(
                                    clientId = clientId,
                                    serverUrl = currentServer,
                                    state = state
                                )
                            }
                            view.setupGitlabButtonListener(gitlabOauthUrl, state)
                            view.enableLoginByGitlab()
                            totalSocialAccountsEnabled++
                        }
                    }

                    getCustomOauthServices(services).let {
                        for (service in it) {
                            val serviceName = getCustomOauthServiceName(service)

                            val customOauthUrl = OauthHelper.getCustomOauthUrl(
                                getCustomOauthHost(service),
                                getCustomOauthAuthorizePath(service),
                                getCustomOauthClientId(service),
                                currentServer,
                                serviceName,
                                state,
                                getCustomOauthScope(service)
                            )

                            view.addCustomOauthServiceButton(
                                customOauthUrl,
                                state,
                                serviceName,
                                getCustomOauthServiceNameColor(service),
                                getCustomOauthButtonColor(service)
                            )
                            totalSocialAccountsEnabled++
                        }
                    }

                    if (totalSocialAccountsEnabled > 0) {
                        view.enableOauthView()
                        if (totalSocialAccountsEnabled > 3) {
                            view.setupFabListener()
                        }
                    } else {
                        view.disableOauthView()
                    }
                } else {
                    view.disableOauthView()
                }
            } catch (exception: RocketChatException) {
                Timber.e(exception)
                view.disableOauthView()
            }
        }
    }

    private fun doAuthentication(loginType: Int, callback: (authenticated: Boolean) -> Unit) {
        launchUI(strategy) {
            view.disableUserInput()
            view.showLoading()
            try {
                val token = retryIO("login") {
                    when (loginType) {
                        TYPE_LOGIN_USER_EMAIL -> {
                            when {
                                settings.isLdapAuthenticationEnabled() ->
                                    client.loginWithLdap(usernameOrEmail, password)
                                usernameOrEmail.isEmail() ->
                                    client.loginWithEmail(usernameOrEmail, password)
                                else ->
                                    client.login(usernameOrEmail, password)
                            }
                        }
                        TYPE_LOGIN_CAS -> {
                            delay(3, TimeUnit.SECONDS)
                            client.loginWithCas(credentialToken)
                        }
                        TYPE_LOGIN_OAUTH -> {
                            client.loginWithOauth(credentialToken, credentialSecret)
                        }
                        TYPE_LOGIN_DEEP_LINK -> {
                            val myself = client.me() // Just checking if the credentials worked.
                            if (myself.id == deepLinkUserId) {
                                Token(deepLinkUserId, deepLinkToken)
                            } else {
                                throw RocketChatAuthException("Invalid Authentication Deep Link Credentials...")
                            }
                        }
                        else -> {
                            throw IllegalStateException("Expected TYPE_LOGIN_USER_EMAIL, TYPE_LOGIN_CAS, TYPE_LOGIN_OAUTH or TYPE_LOGIN_DEEP_LINK")
                        }
                    }
                }
                val username = retryIO("me()") { client.me().username }
                if (username != null) {
                    localRepository.save(LocalRepository.CURRENT_USERNAME_KEY, username)
                    saveAccount(username)
                    saveToken(token)
                    registerPushToken()
                    callback(true)
                } else if (loginType == TYPE_LOGIN_OAUTH) {
                    navigator.toRegisterUsername(token.userId, token.authToken)
                }
            } catch (exception: RocketChatException) {
                when (exception) {
                    is RocketChatTwoFactorException -> {
                        navigator.toTwoFA(usernameOrEmail, password)
                    }
                    else -> {
                        exception.message?.let {
                            view.showMessage(it)
                        }.ifNull {
                            view.showGenericErrorMessage()
                        }
                    }
                }
            } finally {
                view.hideLoading()
                view.enableUserInput()
            }
        }
    }

    private fun doAuthentication(loginType: Int) {
        launchUI(strategy) {
            view.disableUserInput()
            view.showLoading()
            try {
                val token = retryIO("login") {
                    when (loginType) {
                        TYPE_LOGIN_USER_EMAIL -> {
                            when {
                                settings.isLdapAuthenticationEnabled() ->
                                    client.loginWithLdap(usernameOrEmail, password)
                                usernameOrEmail.isEmail() ->
                                    client.loginWithEmail(usernameOrEmail, password)
                                else ->
                                    client.login(usernameOrEmail, password)
                            }
                        }
                        TYPE_LOGIN_CAS -> {
                            delay(3, TimeUnit.SECONDS)
                            client.loginWithCas(credentialToken)
                        }
                        TYPE_LOGIN_OAUTH -> {
                            client.loginWithOauth(credentialToken, credentialSecret)
                        }
                        TYPE_LOGIN_DEEP_LINK -> {
                            val myself = client.me() // Just checking if the credentials worked.
                            if (myself.id == deepLinkUserId) {
                                Token(deepLinkUserId, deepLinkToken)
                            } else {
                                throw RocketChatAuthException("Invalid Authentication Deep Link Credentials...")
                            }
                        }
                        else -> {
                            throw IllegalStateException("Expected TYPE_LOGIN_USER_EMAIL, TYPE_LOGIN_CAS, TYPE_LOGIN_OAUTH or TYPE_LOGIN_DEEP_LINK")
                        }
                    }
                }
                val username = retryIO("me()") { client.me().username }
                if (username != null) {
                    localRepository.save(LocalRepository.CURRENT_USERNAME_KEY, username)
                    saveAccount(username)
                    saveToken(token)
                    registerPushToken()
                    navigator.toChatList()
                } else if (loginType == TYPE_LOGIN_OAUTH) {
                    navigator.toRegisterUsername(token.userId, token.authToken)
                }
            } catch (exception: RocketChatException) {
                when (exception) {
                    is RocketChatTwoFactorException -> {
                        navigator.toTwoFA(usernameOrEmail, password)
                    }
                    else -> {
                        exception.message?.let {
                            view.showMessage(it)
                        }.ifNull {
                            view.showGenericErrorMessage()
                        }
                    }
                }
            } finally {
                view.hideLoading()
                view.enableUserInput()
            }
        }
    }

    private fun getOauthClientId(listMap: List<Map<String, Any>>, serviceName: String): String? {
        return listMap.find { map -> map.containsValue(serviceName) }?.let {
            it["clientId"] ?: it["appId"]
        }.toString()
    }

    private fun getCustomOauthServices(listMap: List<Map<String, Any>>): List<Map<String, Any>>  {
        return listMap.filter { map -> map["custom"] == true }
    }

    private fun getCustomOauthHost(service: Map<String, Any>): String {
        return service["serverURL"].toString()
    }

    private fun getCustomOauthAuthorizePath(service: Map<String, Any>): String {
        return service["authorizePath"].toString()
    }

    private fun getCustomOauthClientId(service: Map<String, Any>): String {
        return service["clientId"].toString()
    }

    private fun getCustomOauthServiceName(service: Map<String, Any>): String {
        return service["service"].toString()
    }

    private fun getCustomOauthScope(service: Map<String, Any>): String {
        return service["scope"].toString()
    }

    private fun getCustomOauthButtonColor(service: Map<String, Any>): Int {
        return service["buttonColor"].toString().parseColor()
    }

    private fun getCustomOauthServiceNameColor(service: Map<String, Any>): Int {
        return service["buttonLabelColor"].toString().parseColor()
    }

    private suspend fun saveAccount(username: String) {
        val icon = settings.favicon()?.let {
            currentServer.serverLogoUrl(it)
        }
        val logo = settings.wideTile()?.let {
            currentServer.serverLogoUrl(it)
        }
        val thumb = currentServer.avatarUrl(username)
        val account = Account(currentServer, icon, logo, username, thumb)
        saveAccountInteractor.save(account)
    }

    private fun saveToken(token: Token) {
        tokenRepository.save(currentServer, token)
    }

    private suspend fun registerPushToken() {
        localRepository.get(LocalRepository.KEY_PUSH_TOKEN)?.let {
            client.registerPushToken(it, getAccountsInteractor.get(), factory)
        }
        // TODO: When the push token is null, at some point we should receive it with
        // onTokenRefresh() on FirebaseTokenService, we need to confirm it.
    }
}