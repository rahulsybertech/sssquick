package com.ssspvtltd.quick.base

import android.util.Log
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.application.MainApplication
import com.ssspvtltd.quick.commonrepo.CommonPDFRepo
import com.ssspvtltd.quick.model.alert.AlertMsg
import com.ssspvtltd.quick.model.order.pending.PendingOrderPDFRegenerateRequest
import com.ssspvtltd.quick.model.progress.ProgressConfig
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.persistance.PrefHelper
import com.ssspvtltd.quick.ui.order.hold.repository.HoldOrderRepository
import com.ssspvtltd.quick.utils.ApiErrorData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var prefHelper: PrefHelper

    @Inject
    lateinit var baseRepository: BaseRepository

    @Inject
    lateinit var commonPDFRepo: CommonPDFRepo

    @Inject
    lateinit var holdOrderRepository: HoldOrderRepository


    var loginErrorApiCallingCount = 0

    private var getPDfURL = MutableLiveData<String>()
    val fetchPdfUrl: LiveData<String> = getPDfURL

    fun updatePdfUrl(url: String) {
        getPDfURL.value = url
    }

    private var holdDeleteMessage = MutableLiveData<String>()
    val fetchHoldDeleteMessage: LiveData<String> = holdDeleteMessage

    fun updateHoldDeleteMessage(message: String) {
        holdDeleteMessage.value = message
    }

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
    val errorLiveData = MutableLiveData<ApiErrorData>()
    fun setErrorObserver(owner: LifecycleOwner, errorObserver: Observer<ApiErrorData>) {
        errorLiveData.observe(owner, errorObserver)
    }

    protected fun apiErrorData(failure: ApiResponse<*>, title: String? = null) {
        errorLiveData.postValue(ApiErrorData(apiResponse = failure, titleMsg = title))
    }


    private val loginStatusLiveData = MutableLiveData<Boolean>()
    fun setLoginStatusObserver(owner: LifecycleOwner, loginStatusObserver: Observer<Boolean>) {
        loginStatusLiveData.observe(owner, loginStatusObserver)
    }

    protected fun apiLoginStatusData(loginStatus: Boolean) {
        loginStatusLiveData.postValue(loginStatus)
    }


    fun callLoginStatusApi() {
        viewModelScope.launch(Dispatchers.IO) {
            // showProgressBar(ProgressConfig("Checking Login Status\nPlease wait..."))
            when (val response = baseRepository.autoLogout()) {
                is ResultWrapper.Failure -> {

                    Log.i("TaG", "status -=-=-=-=-=-==-=--=-=-->${response.error}")
                    // apiErrorData(response.error)
                }

                is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                    withContext(Dispatchers.Main) {
                        hideProgressBar()
                        apiLoginStatusData(response.value.data?.loginStatus ?: false)
                        // apiLoginStatusData(false)
                    }
                }
            }
        }
    }

    fun logout() = viewModelScope.launch {
        showProgressBar(ProgressConfig("Logout\nPlease wait..."))
        when (val response = baseRepository.logout(prefHelper.getMarketerMobile().toString())) {
            is ResultWrapper.Failure -> {
                apiErrorData(response.error)
            }

            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                withContext(Dispatchers.Main) {
                    hideProgressBar()
                    prefHelper.logout()
                }
            }
        }
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
        @ColorRes btnBgColor: Int = R.color.error_text,
        callback: (() -> Unit)? = null
    ) {
        alertMsgLiveData.postValue(
            AlertMsg(title, message, type, cancelable, okButtonText, barColor, btnBgColor, callback)
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


    fun getPDF(pendingOrderPDFRegenerateRequest: PendingOrderPDFRegenerateRequest.PendingOrderPDFRegenerateRequestItem) =
        viewModelScope.launch {
            showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
            when (val response =
                commonPDFRepo.callOrderBookGeneratePdf(pendingOrderPDFRegenerateRequest)) {
                is ResultWrapper.Failure -> {
                    println("ERROR_IN_RESPONSE ${response.error}")
                    apiErrorData(response.error)
                }

                is ResultWrapper.Success -> {
                    hideProgressBar()
                    println("ERROR_IN_RESPONSE 2 ${response.value.body()?.data}")
                //    getPDfURL.value = response.value.body()?.data!!
                    val pdfUrl = response.value.body()?.data
                    pdfUrl?.let {
                        getPDfURL.value = it
                    } ?: run {
                        println("ERROR: PDF URL is null")
                       // apiErrorData(response.er)
                    }
                }
            }
        }

    fun deleteOrder(orderId: String) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Deleting Data..."))
        when (val response =
            holdOrderRepository.holdOrderDelete(orderId)) {
            is ResultWrapper.Failure -> {
                println("ERROR_IN_RESPONSE ${response.error}")
                apiErrorData(response.error)
            }

            is ResultWrapper.Success -> {
                hideProgressBar()
                println("ERROR_IN_RESPONSE 2 ${response.value.message}")
                holdDeleteMessage.value = response.value.message!!
            }
        }
    }
}