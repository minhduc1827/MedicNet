package com.medicnet.android.authentication.login.ui

import DrawableHelper
import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.style.ClickableSpan
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import chat.rocket.common.util.ifNull
import com.medicnet.android.BuildConfig
import com.medicnet.android.R
import com.medicnet.android.authentication.domain.model.LoginDeepLinkInfo
import com.medicnet.android.authentication.login.presentation.LoginPresenter
import com.medicnet.android.authentication.login.presentation.LoginView
import com.medicnet.android.authentication.ui.AuthenticationActivity
import com.medicnet.android.helper.TextHelper
import com.medicnet.android.util.AppUtil
import com.medicnet.android.util.extensions.*
import com.medicnet.android.webview.cas.ui.INTENT_CAS_TOKEN
import com.medicnet.android.webview.cas.ui.casWebViewIntent
import com.medicnet.android.webview.oauth.ui.INTENT_OAUTH_CREDENTIAL_SECRET
import com.medicnet.android.webview.oauth.ui.INTENT_OAUTH_CREDENTIAL_TOKEN
import com.medicnet.android.webview.oauth.ui.oauthWebViewIntent
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_authentication_log_in.*
import javax.inject.Inject


internal const val REQUEST_CODE_FOR_CAS = 1
internal const val REQUEST_CODE_FOR_OAUTH = 2

class LoginFragment : Fragment(), LoginView {
    @Inject
    lateinit var presenter: LoginPresenter
    private var isOauthViewEnable = false
    private val layoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        areLoginOptionsNeeded()
    }
    private var isGlobalLayoutListenerSetUp = false
    private var deepLinkInfo: LoginDeepLinkInfo? = null
    lateinit var context: AuthenticationActivity

    companion object {
        private const val DEEP_LINK_INFO = "DeepLinkInfo"

        fun newInstance(deepLinkInfo: LoginDeepLinkInfo? = null) = LoginFragment().apply {
            arguments = Bundle().apply {
                putParcelable(DEEP_LINK_INFO, deepLinkInfo)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        deepLinkInfo = arguments?.getParcelable(DEEP_LINK_INFO)

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? =
            container?.inflate(R.layout.fragment_authentication_log_in)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            tintEditTextDrawableStart()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity!!.getWindow().apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }

            window.statusBarColor = ContextCompat.getColor(view.context, R.color.status_bar_color)
        }
        context = activity as AuthenticationActivity
        deepLinkInfo?.let {
            presenter.authenticateWithDeepLink(it)
        }.ifNull {
            presenter.setupView()
        }
        if (BuildConfig.BUILD_TYPE.equals("debug")) {//add cheat to login
            ui {
                text_username_or_email.setOnLongClickListener {
                    presenter.authenticateWithUserAndPassword(
                            "martin.siebachmeyer@NHS.net",
                            "P@ssword123!") { authenticated ->
                        if (authenticated) {
                            AppUtil.displayLockScreen(activity, true, context.LOCKSCREEN_REQUEST_CODE)
                        }
                    }
                    true
                }
            }
        }
        button_create_new_account.setOnClickListener {
            context.organzation = ""
            context.role = ""
            presenter.signup()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isGlobalLayoutListenerSetUp) {
            scroll_view.viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
            isGlobalLayoutListenerSetUp = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_FOR_CAS) {
                data?.apply {
                    presenter.authenticateWithCas(getStringExtra(INTENT_CAS_TOKEN))
                }
            } else if (requestCode == REQUEST_CODE_FOR_OAUTH) {
                data?.apply {
                    presenter.authenticateWithOauth(
                            getStringExtra(INTENT_OAUTH_CREDENTIAL_TOKEN),
                            getStringExtra(INTENT_OAUTH_CREDENTIAL_SECRET)
                    )
                }
            }
        }
    }

    private fun tintEditTextDrawableStart() {
        ui {
            val personDrawable =
                    DrawableHelper.getDrawableFromId(R.drawable.ic_assignment_ind_black_24dp, it)
            val lockDrawable = DrawableHelper.getDrawableFromId(R.drawable.ic_lock_black_24dp, it)

            val drawables = arrayOf(personDrawable, lockDrawable)
            DrawableHelper.wrapDrawables(drawables)
            DrawableHelper.tintDrawables(drawables, it, R.color.colorDrawableTintGrey)
            DrawableHelper.compoundDrawables(
                    arrayOf(text_username_or_email, text_password),
                    drawables
            )
        }
    }

    override fun showLoading() {
        ui {
            view_loading.isVisible = true
        }
    }

    override fun hideLoading() {
        ui {
            view_loading.isVisible = false
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

    override fun showGenericErrorMessage() {
        showMessage(R.string.msg_generic_error)
    }

    override fun showFormView() {
        ui {
            text_username_or_email.isVisible = true
            text_password.isVisible = true
        }
    }

    override fun hideFormView() {
        ui {
            text_username_or_email.isVisible = false
            text_password.isVisible = false
        }
    }

    override fun setupLoginButtonListener() {
        ui {
            button_log_in.setOnClickListener {
                presenter.authenticateWithUserAndPassword(
                        text_username_or_email.textContent,
                        text_password.textContent
                ) { authenticated ->
                    if (authenticated) {
                        AppUtil.displayLockScreen(activity, true, context.LOCKSCREEN_REQUEST_CODE)
                    }
                }
            }
        }
    }

    override fun enableUserInput() {
        ui {
            button_log_in.isEnabled = true
            text_username_or_email.isEnabled = true
            text_password.isEnabled = true
        }
    }

    override fun disableUserInput() {
        ui {
            button_log_in.isEnabled = false
            text_username_or_email.isEnabled = false
            text_password.isEnabled = false
        }
    }

    override fun showCasButton() {
        ui {
            button_cas.isVisible = true
        }
    }

    override fun hideCasButton() {
        ui {
            button_cas.isVisible = false
        }
    }

    override fun setupCasButtonListener(casUrl: String, casToken: String) {
        ui { activity ->
            button_cas.setOnClickListener {
                startActivityForResult(
                        activity.casWebViewIntent(casUrl, casToken),
                        REQUEST_CODE_FOR_CAS
                )
                activity.overridePendingTransition(R.anim.slide_up, R.anim.hold)
            }
        }
    }

    override fun showSignUpView() {
        ui {
            //            text_new_to_rocket_chat.isVisible = true
        }
    }

    override fun setupSignUpView() {
        ui {
            val signUp = getString(R.string.title_sign_up)
            val newToRocketChat = String.format(getString(R.string.msg_new_user), signUp)

            text_new_to_rocket_chat.text = newToRocketChat

            val signUpListener = object : ClickableSpan() {
                override fun onClick(view: View) = presenter.signup()
            }

            TextHelper.addLink(text_new_to_rocket_chat, arrayOf(signUp), arrayOf(signUpListener))
        }
    }

    override fun showForgotPasswordView() {
        ui {
            text_forgot_your_password.isVisible = true
        }
    }

    override fun setupForgotPasswordView() {
        ui {
            val reset = getString(R.string.msg_reset)
            val forgotPassword = String.format(getString(R.string.msg_forgot_password), reset)

//            text_forgot_your_password.text = forgotPassword

            /*val resetListener = object : ClickableSpan() {
                override fun onClick(view: View) = presenter.forgotPassword()
            }

            TextHelper.addLink(text_forgot_your_password, arrayOf(reset), arrayOf(resetListener))*/
            text_forgot_your_password.setOnClickListener {
                //                presenter.forgotPassword()
                AppUtil.showAlerDialog(activity, getString(R.string.label_forgot_password_dlg), getString(R.string.forgot_password_msg_dlg), false, getString(R.string.msg_ok))
            }
        }
    }

    override fun hideSignUpView() {
        ui {
            text_new_to_rocket_chat.isVisible = false
        }
    }

    override fun enableOauthView() {
        ui {
            isOauthViewEnable = true
            showThreeSocialAccountsMethods()
            social_accounts_container.isVisible = true
        }
    }

    override fun disableOauthView() {
        ui {
            isOauthViewEnable = false
            social_accounts_container.isVisible = false
        }
    }

    override fun showLoginButton() {
        ui {
            button_log_in.isVisible = true
        }
    }

    override fun hideLoginButton() {
        ui {
            button_log_in.isVisible = false
        }
    }

    override fun enableLoginByFacebook() {
        ui {
            button_facebook.isClickable = true
        }
    }

    override fun setupFacebookButtonListener(facebookOauthUrl: String, state: String) {
        ui { activity ->
            button_facebook.setOnClickListener {
                startActivityForResult(
                        activity.oauthWebViewIntent(facebookOauthUrl, state),
                        REQUEST_CODE_FOR_OAUTH
                )
                activity.overridePendingTransition(R.anim.slide_up, R.anim.hold)
            }
        }
    }

    override fun enableLoginByGithub() {
        ui {
            button_github.isClickable = true
        }
    }

    override fun setupGithubButtonListener(githubUrl: String, state: String) {
        ui { activity ->
            button_github.setOnClickListener {
                startActivityForResult(
                        activity.oauthWebViewIntent(githubUrl, state),
                        REQUEST_CODE_FOR_OAUTH
                )
                activity.overridePendingTransition(R.anim.slide_up, R.anim.hold)
            }
        }
    }

    override fun enableLoginByGoogle() {
        ui {
            button_google.isClickable = true
        }
    }

    // TODO: Use custom tabs instead of web view.
    // See https://github.com/RocketChat/Rocket.Chat.Android/issues/968
    override fun setupGoogleButtonListener(googleUrl: String, state: String) {
        ui { activity ->
            button_google.setOnClickListener {
                startActivityForResult(
                        activity.oauthWebViewIntent(googleUrl, state),
                        REQUEST_CODE_FOR_OAUTH
                )
                activity.overridePendingTransition(R.anim.slide_up, R.anim.hold)
            }
        }
    }

    override fun enableLoginByLinkedin() {
        ui {
            button_linkedin.isClickable = true
        }
    }

    override fun setupLinkedinButtonListener(linkedinUrl: String, state: String) {
        ui { activity ->
            button_linkedin.setOnClickListener {
                startActivityForResult(
                        activity.oauthWebViewIntent(linkedinUrl, state),
                        REQUEST_CODE_FOR_OAUTH
                )
                activity.overridePendingTransition(R.anim.slide_up, R.anim.hold)
            }
        }
    }

    override fun enableLoginByMeteor() {
        ui {
            button_meteor.isClickable = true
        }
    }

    override fun enableLoginByTwitter() {
        ui {
            button_twitter.isClickable = true
        }
    }

    override fun enableLoginByGitlab() {
        ui {
            button_gitlab.isClickable = true
        }
    }

    override fun setupGitlabButtonListener(gitlabUrl: String, state: String) {
        ui { activity ->
            button_gitlab.setOnClickListener {
                startActivityForResult(
                        activity.oauthWebViewIntent(gitlabUrl, state),
                        REQUEST_CODE_FOR_OAUTH
                )
                activity.overridePendingTransition(R.anim.slide_up, R.anim.hold)
            }
        }
    }

    override fun addCustomOauthServiceButton(
            customOauthUrl: String,
            state: String,
            serviceName: String,
            serviceNameColor: Int,
            buttonColor: Int
    ) {
        ui { activity ->
            val button = getCustomOauthButton(serviceName, serviceNameColor, buttonColor)
            social_accounts_container.addView(button)

            button.setOnClickListener {
                startActivityForResult(
                        activity.oauthWebViewIntent(customOauthUrl, state),
                        REQUEST_CODE_FOR_OAUTH
                )
                activity.overridePendingTransition(R.anim.slide_up, R.anim.hold)
            }
        }
    }

    override fun setupFabListener() {
        ui {
            button_fab.isVisible = true
            button_fab.setOnClickListener({
                button_fab.hide()
                showRemainingSocialAccountsView()
                scrollToBottom()
            })
        }
    }

    override fun setupGlobalListener() {
        // We need to setup the layout to hide and show the oauth interface when the soft keyboard
        // is shown (which means that the user has touched the text_username_or_email or
        // text_password EditText to fill that respective fields).
        /* if (!isGlobalLayoutListenerSetUp) {
             scroll_view.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
             isGlobalLayoutListenerSetUp = true
         }*/
    }

    override fun alertWrongUsernameOrEmail() {
        ui {
            vibrateSmartPhone()
            text_username_or_email.shake()
            text_username_or_email.requestFocus()
        }
    }

    override fun alertWrongPassword() {
        ui {
            vibrateSmartPhone()
            text_password.shake()
            text_password.requestFocus()
        }
    }

    private fun showRemainingSocialAccountsView() {
        social_accounts_container.postDelayed(300) {
            ui {
                (0..social_accounts_container.childCount)
                        .mapNotNull { social_accounts_container.getChildAt(it) as? ImageButton }
                        .filter { it.isClickable }
                        .forEach { it.isVisible = true }
            }
        }
    }

    // Scrolling to the bottom of the screen.
    private fun scrollToBottom() {
        /*scroll_view.postDelayed(1250) {
            ui {
                scroll_view.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }*/
    }


    private fun areLoginOptionsNeeded() {
        /*if (!isEditTextEmpty() || KeyboardHelper.isSoftKeyboardShown(scroll_view.rootView)) {
            hideSignUpView()
            hideOauthView()
            showLoginButton()
        } else {
            showSignUpView()
            showOauthView()
            hideLoginButton()
        }*/
    }

    // Returns true if *all* EditTexts are empty.
    private fun isEditTextEmpty(): Boolean {
        return text_username_or_email.textContent.isBlank() && text_password.textContent.isEmpty()
    }

    private fun showThreeSocialAccountsMethods() {
        (0..social_accounts_container.childCount)
                .mapNotNull { social_accounts_container.getChildAt(it) as? ImageButton }
                .filter { it.isClickable }
                .take(3)
                .forEach { it.isVisible = true }
    }

    private fun showOauthView() {
        if (isOauthViewEnable) {
            social_accounts_container.isVisible = true
            if (enabledSocialAccounts() > 3) {
                button_fab.isVisible = true
            }
        }
    }

    private fun hideOauthView() {
        if (isOauthViewEnable) {
            social_accounts_container.isVisible = false
            button_fab.isVisible = false
        }
    }

    private fun enabledSocialAccounts(): Int {
        return enabledOauthAccountsImageButtons() + enabledServicesAccountsButtons()
    }

    private fun enabledOauthAccountsImageButtons(): Int {
        return (0..social_accounts_container.childCount)
                .mapNotNull { social_accounts_container.getChildAt(it) as? ImageButton }
                .filter { it.isClickable }
                .size
    }

    private fun enabledServicesAccountsButtons(): Int {
        return (0..social_accounts_container.childCount)
                .mapNotNull { social_accounts_container.getChildAt(it) as? Button }
                .size
    }

    /**
     * Gets a stylized custom OAuth button.
     */
    private fun getCustomOauthButton(
            buttonText: String,
            buttonTextColor: Int,
            buttonBgColor: Int
    ): Button {
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val margin = resources.getDimensionPixelSize(R.dimen.screen_edge_left_and_right_margins)
        params.setMargins(margin, margin, margin, 0)

        val button = Button(context)
        button.layoutParams = params
        button.text = buttonText
        button.setTextColor(buttonTextColor)
        button.background.setColorFilter(buttonBgColor, PorterDuff.Mode.MULTIPLY)

        return button
    }
}