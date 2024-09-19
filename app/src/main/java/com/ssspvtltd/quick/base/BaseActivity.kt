package com.ssspvtltd.quick.base

import android.os.Bundle
import android.view.LayoutInflater
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
import tech.developingdeveloper.toaster.Toaster

typealias InflateA<T> = (LayoutInflater) -> T

abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity() {
    protected lateinit var binding: VB
    protected lateinit var viewModel: VM

    abstract val inflate: InflateA<VB>
    abstract fun initViewModel(): VM

    private var progressDialog: SweetAlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate.invoke(layoutInflater)
        setContentView(binding.root)
        viewModel = initViewModel()
        viewModel.setErrorObserver(this, errorObserver)
        viewModel.setProgressBarObserver(this, progressbarObserver)
        viewModel.setAlertMsgObserver(this, alertMsgObserver)
    }

    val errorObserver = Observer<ApiResponse<*>> {
        hideProgressBar()
        handleApiErrorResponse(it)
    }


    val progressbarObserver = Observer<Pair<Boolean, ProgressConfig?>> {
        if (it.first) showProgressBar(it.second)
        else hideProgressBar()
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

    private fun handleApiErrorResponse(errorResponse: ApiResponse<*>) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).apply {
            titleText = "Sorry!"
            contentText = errorResponse.message ?: getString(R.string.something_went_wrong)
            setCancelable(false)
            confirmText = getString(R.string.ok)
            confirmButtonBackgroundColor = getColor(R.color.error_text)
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