package com.ssspvtltd.quick.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.snackbar.Snackbar
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.model.alert.AlertMsg
import com.ssspvtltd.quick.model.progress.ProgressConfig
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.ui.auth.activity.LoginActivity
import com.ssspvtltd.quick.ui.splash.SplashActivity
import com.ssspvtltd.quick.utils.ApiErrorData
import kotlinx.coroutines.runBlocking
import tech.developingdeveloper.toaster.Toaster

typealias InflateA<T> = (LayoutInflater) -> T

abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity() {
    protected lateinit var binding: VB
    protected lateinit var viewModel: VM

    abstract val inflate: InflateA<VB>
    abstract fun initViewModel(): VM

    private var progressDialog: SweetAlertDialog? = null

    override fun onResume() {
        super.onResume()
        if (this !is LoginActivity && this !is SplashActivity) {
            viewModel.callLoginStatusApi()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate.invoke(layoutInflater)
        setContentView(binding.root)
        viewModel = initViewModel()
        viewModel.setErrorObserver(this, errorObserver)
        viewModel.setProgressBarObserver(this, progressbarObserver)
        viewModel.setAlertMsgObserver(this, alertMsgObserver)
        viewModel.setLoginStatusObserver(this, loginStatusObserver)
    }

    val errorObserver = Observer<ApiErrorData> {
        hideProgressBar()
        handleApiErrorResponse(it.apiResponse, it.titleMsg)
    }


    val progressbarObserver = Observer<Pair<Boolean, ProgressConfig?>> {
        if (it.first) showProgressBar(it.second)
        else hideProgressBar()
    }

    val loginStatusObserver = Observer<Boolean> { isUserLogin ->
        if (!isUserLogin) {

            if (this !is LoginActivity && this !is SplashActivity) {

                val dialog = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.session_expired_title))
                    .setMessage(getString(R.string.you_have_been_logout_by_adm))
                    .setPositiveButton(getString(R.string.ok)) { _, _ ->
                        viewModel.logout()
                        runBlocking { viewModel.prefHelper.clearPref() }
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finishAffinity()

                    }
                    .setCancelable(false)
                    .create()
                dialog.show()

            }

        } else {
            hideProgressBar()
        }


    }

    val alertMsgObserver = Observer<AlertMsg> {
        showAlertMsg(it)
    }


    private fun showProgressBar(config: ProgressConfig? = ProgressConfig()) {
        if (!isFinishing && !isDestroyed) {
            progressDialog?.dismissWithAnimation()
            progressDialog = null
            progressDialog = SweetAlertDialog(this, config?.type ?: SweetAlertDialog.PROGRESS_TYPE)
            config?.title?.let { progressDialog?.titleText = it }
            config?.message?.let { progressDialog?.contentText = it }
            progressDialog?.setCancelable(config?.cancelable ?: false)
            config?.barColor?.let { progressDialog?.progressHelper?.barColor = getColor(it) }
            progressDialog?.show()
        }
    }

    private fun hideProgressBar() {
        if (!isFinishing && !isDestroyed) {
            progressDialog?.dismissWithAnimation()
            progressDialog = null
        }
    }

    private fun showAlertMsg(config: AlertMsg? = AlertMsg()) {
        SweetAlertDialog(this, config?.type!!).apply {
            titleText = config.title
            contentText = config.message
            setCancelable(false)
            confirmText = config.okButtonText
            confirmButtonBackgroundColor = getColor(config.btnBgColor)
            setConfirmClickListener {
                dismiss()
                config.isOkCallBack?.let { it1 -> it1() }
            }
        }.show()
    }


    private fun dismisAlertMsg() {
        if (!isFinishing && !isDestroyed) {
            progressDialog?.dismissWithAnimation()
            progressDialog = null
        }
    }

    fun addFragment(fragment: Fragment, container: Int, addToBackStack: Boolean = true) {
        supportFragmentManager.beginTransaction().apply {
            add(container, fragment, fragment::class.java.name)
            if (addToBackStack) addToBackStack(fragment::class.java.name)
        }.commit()
    }

    fun replaceFragment(fragment: Fragment, container: Int, addToBackStack: Boolean = true) {
        supportFragmentManager.beginTransaction().apply {
            replace(container, fragment, fragment::class.java.name)
            if (addToBackStack) addToBackStack(fragment::class.java.name)
        }.commit()
    }

    fun showToast(message: String?, duration: Int = Toaster.LENGTH_SHORT) {
        if (message.isNullOrBlank()) return
        Toaster.pop(this, message, duration).show()
    }

    fun showSnackBar(message: String?, duration: Int = Snackbar.LENGTH_SHORT) {
        if (message.isNullOrBlank()) return
        Snackbar.make(binding.root, message, duration).show()
    }

    private fun handleApiErrorResponse(errorResponse: ApiResponse<*>, title: String?) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).apply {
            titleText = title ?: getString(R.string.sorry)
            contentText = errorResponse.message ?: getString(R.string.something_went_wrong)
            confirmText = getString(R.string.ok)
            setConfirmClickListener {
                if (errorResponse.message.equals("Token is expired or invalid.")) {
                    dismiss()
                    viewModel.logout()
                    runBlocking { viewModel.prefHelper.clearPref() }
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finishAffinity()
                } else {
                    dismiss()
                }
            }

            confirmButtonBackgroundColor = getColor(R.color.error_text)
            setCancelable(false)
        }.show()

        // when (errorResponse.apiRequestCode) {
        //     ApiRequestCode.LOGIN.ordinal -> {
        //         SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).apply {
        //             titleText = "Sorry!"
        //             contentText = errorResponse.message ?: getString(R.string.something_went_wrong)
        //             setCancelable(false)
        //             confirmText = getString(R.string.ok)
        //         }.show()
        //     }
        //
        //     else -> {
        //         SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).apply {
        //             titleText = "Sorry!"
        //             contentText = errorResponse.message ?: getString(R.string.something_went_wrong)
        //             setCancelable(false)
        //             confirmText = getString(R.string.ok)
        //         }.show()
        //     }
        // }
    }
}