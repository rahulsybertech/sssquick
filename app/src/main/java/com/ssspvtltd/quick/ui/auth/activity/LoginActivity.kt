package com.ssspvtltd.quick.ui.auth.activity

import android.app.Dialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseActivity
import com.ssspvtltd.quick.base.InflateA
import com.ssspvtltd.quick.databinding.ActivityLoginBinding
import com.ssspvtltd.quick.model.POLICY_WEBVIEW
import com.ssspvtltd.quick.model.TERM_WEBVIEW
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.ui.auth.viewmodel.LoginViewModel
import com.ssspvtltd.quick.ui.main.MainActivity
import com.ssspvtltd.quick.utils.extension.dp
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.isPhoneNumber
import com.ssspvtltd.quick.utils.versionName
import dagger.hilt.android.AndroidEntryPoint
import `in`.aabhasjindal.otptextview.OTPListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {

    override val inflate: InflateA<ActivityLoginBinding> get() = ActivityLoginBinding::inflate
    override fun initViewModel(): LoginViewModel = getViewModel<LoginViewModel>()

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerObservers()
        registerListeners()
    }

    private fun registerObservers() {

        viewModel.errorLiveData.observe(this) {
            if (it.apiResponse.apiRequestCode == ApiRequestCode.VERIFY_OTP.ordinal) {
                binding.otpView.showError()
                binding.next.setBackgroundResource(R.drawable.bg_btn_effect_red)
            }
        }


       /* binding.tvDesc.setOnClickListener {
            changeAppIcon("com.ssspvtltd.quick.IconOneAlias")
        }

        binding.mobileTxt.setOnClickListener {
            changeAppIcon("com.ssspvtltd.quick.IconTwoAlias")
        }*/
        viewModel.loginData.observe(this) {
            if (it == null) return@observe
            binding.llOtp.visibility = View.VISIBLE
            binding.mobile.isEnabled = false
            binding.mobile.setTextColor(getColor(R.color.black))
            binding.llView.setBackgroundResource(R.color.grey_300)
            binding.next.setBackgroundResource(R.drawable.bg_btn_effect_red)
            binding.next.setText(R.string.verify_otp)
            binding.editMobile.visibility = View.VISIBLE
            binding.scroll.fullScroll(View.FOCUS_DOWN)
            setResendTime()
        }
        viewModel.verifyOtpData.observe(this) {
            if (it == null) return@observe
            showToast(it.message.orEmpty())
            Intent(this, MainActivity::class.java).apply {
                putExtra("mobile", binding.mobile.text.toString())
                startActivity(this)
                finishAffinity()
            }
        }
        viewModel.resendOtpData.observe(this) {
            if (it == null) return@observe
            setResendTime()
        }
    }

    private fun registerListeners() = with(binding) {

        binding.tvVersion.text = versionName()

        binding.scroll.fullScroll(View.FOCUS_DOWN)

        llPolicy.setOnClickListener {
            showWebViewDialog(POLICY_WEBVIEW)
        }
        llTerms.setOnClickListener {

            showWebViewDialog(TERM_WEBVIEW)
        }

        mobile.doAfterTextChanged {
            when {
                (it?.length ?: 0) < 10 -> {
                    mobile.error = null
                    mobile.setTextColor(getColor(R.color.black))
                    next.setBackgroundResource(R.drawable.bg_btn_effect_red)
                    llView.setBackgroundResource(R.color.gray)
                    llView.layoutParams.height = 2.dp
                }

                it?.isPhoneNumber() == true -> {
                    mobile.error = null
                    mobile.setTextColor(getColor(R.color.green))
                    next.setBackgroundResource(R.drawable.bg_btn_effect_green)
                    llView.setBackgroundResource(R.color.green)
                    llView.layoutParams.height = 4.dp
                }

                else -> {
                    mobile.error = getString(R.string.invalid_mobile_number)
                    mobile.setTextColor(getColor(R.color.error_text))
                    next.setBackgroundResource(R.drawable.bg_btn_effect_red)
                    llView.setBackgroundResource(R.color.error_text)
                    llView.layoutParams.height = 4.dp
                }
            }
        }
        next.setOnClickListener {
            if (!validate()) return@setOnClickListener
            val userName = runBlocking { viewModel.prefHelper.getUserName() ?: "" }
            if (userName.isBlank()) {
                viewModel.userLogin(binding.mobile.text.toString())
            } else {
                if (otpView.otp.toString().length >= 4) {
                    viewModel.verifyOTP(mobile.text.toString(), otpView.otp)
                } else {
                    otpView.showError()
                    otpView.requestFocusOTP()
                    showToast("Please Enter 4 digit valid otp")
                }
            }
        }
        binding.editMobile.setOnClickListener {
            lifecycleScope.launch {
                viewModel.prefHelper.clearPref()
            }
            binding.next.setBackgroundResource(R.drawable.bg_btn_effect_green)
            binding.llOtp.visibility = View.GONE
            binding.mobile.isEnabled = true
            binding.mobile.requestFocus()
            binding.mobile.setTextColor(getColor(R.color.green))
            binding.llView.setBackgroundResource(R.color.green)
            binding.next.text = "Generate Otp"
            binding.editMobile.visibility = View.GONE
            binding.llView.layoutParams.height = 5
        }
        binding.resendOtp.setOnClickListener {
            viewModel.resendOTP(binding.mobile.text.toString())
        }

        otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                if (binding.otpView.otp!!.isNotEmpty()) {
                    binding.scroll.fullScroll(View.FOCUS_DOWN)
                }
                if (binding.otpView.otp?.length != 4) {
                    binding.next.setBackgroundResource(R.drawable.bg_btn_effect_red)
                }
            }

            override fun onOTPComplete(otp: String) {
                binding.next.setBackgroundResource(R.drawable.bg_btn_effect_green)
//                Toast.makeText(this@LoginActivity, "The OTP is $otp", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun showWebViewDialog(url: String) {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.webview_dialog)
        webView = dialog.findViewById(R.id.webView)
        val closeBtn: AppCompatImageButton = dialog.findViewById(R.id.closeBtn)

        closeBtn.setOnClickListener {
            webView.clearHistory()
            dialog.dismiss()
        }
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?, request: WebResourceRequest?
            ): Boolean {
                return true
            }
        }
        webView.loadUrl(url)
        dialog.show()

    }


    private fun validate(): Boolean {
        if (!binding.mobile.text.toString().isPhoneNumber()) {
            binding.mobile.requestFocus()
            binding.llView.setBackgroundResource(R.color.error_text)
            binding.mobile.error = getString(R.string.invalid_mobile_number)
            return false
        }
        return true
    }

    private fun setResendTime() {
        val timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val va = (millisUntilFinished % 60000 / 1000).toInt()
                binding.otpDesc.text = "Seconds remaining : ${String.format("%02d", va)}"
                binding.resendOtp.visibility = View.GONE
            }

            override fun onFinish() {
                binding.otpDesc.text = "Didn't receive the OTP?"
                binding.resendOtp.visibility = View.VISIBLE
            }
        }
        timer.start()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val accessToken = viewModel.prefHelper.getAccessToken() ?: ""
            if (accessToken.isBlank()) viewModel.prefHelper.setUserName("")
        }
    }


    //Change the launcher icon by rahul
    private fun changeAppIcon(aliasName: String) {
        val pm = packageManager

        // Disable all aliases first
        pm.setComponentEnabledSetting(
            ComponentName(this, "com.ssspvtltd.quick.IconOneAlias"),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        pm.setComponentEnabledSetting(
            ComponentName(this, "com.ssspvtltd.quick.IconTwoAlias"),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )

        // Enable the selected alias (icon)
        pm.setComponentEnabledSetting(
            ComponentName(this, aliasName),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}