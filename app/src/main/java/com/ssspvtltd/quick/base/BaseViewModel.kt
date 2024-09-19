package com.ssspvtltd.quick.base

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import cn.pedant.SweetAlert.SweetAlertDialog
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.application.MainApplication
import com.ssspvtltd.quick.model.alert.AlertMsg
import com.ssspvtltd.quick.model.progress.ProgressConfig
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.persistance.PrefHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var prefHelper: PrefHelper

    /**
     * ProgressBar
     */
    private val progressBarLiveData = MutableLiveData<Pair<Boolean, ProgressConfig?>>()
    fun setProgressBarObserver(
        owner: LifecycleOwner,
        observer: Observer<Pair<Boolean, ProgressConfig?>>
    ) {
        progressBarLiveData.observe(owner, observer)
    }

    fun showProgressBar(config: ProgressConfig = ProgressConfig()) {
        progressBarLiveData.postValue(Pair(true, config))
    }

    fun hideProgressBar() {
        progressBarLiveData.postValue(Pair(false, null))
    }

    /**
     * Error Handing
     */
    val errorLiveData = MutableLiveData<ApiResponse<*>>()
    fun setErrorObserver(owner: LifecycleOwner, errorObserver: Observer<ApiResponse<*>>) {
        errorLiveData.observe(owner, errorObserver)
    }

    protected fun apiErrorData(failure: ApiResponse<*>) {
        errorLiveData.postValue(failure)
    }

    /**
     * Alert
     */
    private val alertMsgLiveData = MutableLiveData<AlertMsg>()
    fun setAlertMsgObserver(
        owner: LifecycleOwner,
        observer: Observer<AlertMsg>
    ) {
        alertMsgLiveData.observe(owner, observer)
    }

    fun showMsgAlert(
        title: String? = null,
        message: String? = null,
        type: Int = SweetAlertDialog.ERROR_TYPE,
        cancelable: Boolean = false,
        okButtonText: String = "OK",
        @ColorRes barColor: Int = R.color.color_A5DC86,
        @ColorRes btnBgColor: Int = R.color.error_text
    ) {
        alertMsgLiveData.postValue(
            AlertMsg(title, message, type, cancelable, okButtonText, barColor, btnBgColor)
        )
    }

    protected fun showMsgAlert(
        @StringRes title: Int? = null,
        @StringRes message: Int? = null,
        type: Int = SweetAlertDialog.ERROR_TYPE,
        cancelable: Boolean = false,
        @StringRes okButtonText: Int = R.string.ok,
        @ColorRes barColor: Int = R.color.color_A5DC86,
        @ColorRes btnBgColor: Int = R.color.error_text
    ) {
        alertMsgLiveData.postValue(
            AlertMsg(
                title = getString(title),
                message = getString(message),
                type = type,
                cancelable = cancelable,
                okButtonText = getString(okButtonText),
                barColor = barColor,
                btnBgColor = btnBgColor
            )
        )
    }


    protected fun getString(@StringRes id: Int?, vararg args: Any): String? {
        if (id == null) return null
        return MainApplication.localeContext.getString(id, *args)
    }
}