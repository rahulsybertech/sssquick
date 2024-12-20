package com.ssspvtltd.quick.ui.auth.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.model.auth.LoginData
import com.ssspvtltd.quick.model.auth.VerifyOtpData
import com.ssspvtltd.quick.model.progress.ProgressConfig
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.ui.auth.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
    @ApplicationContext private val mContext: Context
) : BaseViewModel() {

    private val _loginData = MutableLiveData<LoginData?>()
    val loginData: LiveData<LoginData?> get() = _loginData

    private val _verifyOtpData = MutableLiveData<ApiResponse<VerifyOtpData>?>()
    val verifyOtpData: LiveData<ApiResponse<VerifyOtpData>?> get() = _verifyOtpData

    private val _resendOtpData = MutableLiveData<ApiResponse<*>?>()
    val resendOtpData:LiveData<ApiResponse<*>?> get() = _resendOtpData

    private val _logoutData = MutableLiveData<ApiResponse<*>?>()
    val logoutData: LiveData<ApiResponse<*>?> get() = _logoutData

    private val _loginStatus = MutableLiveData<Boolean?>()
    val loginStatus: LiveData<Boolean?> get() = _loginStatus



    fun userLogin(mobileNo: String?) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Verifying mobile number\nPlease wait..."))
        when (val response = repository.loginUser(mobileNo)) {
            is ResultWrapper.Failure -> {
                apiErrorData(response.error)
            }
            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                //logic goes here(loops, if else, data manipulation)
                prefHelper.setUserName(response.value.data?.name)
                withContext(Dispatchers.Main) {
                       hideProgressBar()
                    _loginData.postValue(response.value.data)
                }
            }
        }
    }

    fun verifyOTP(mobileNo: String?, otp:String?) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Verifying OTP\nPlease wait..."))
        when (val response = repository.verifyOTP(mobileNo, otp)) {
            is ResultWrapper.Failure -> {
                Toast.makeText(mContext, response.error.message, Toast.LENGTH_SHORT).show()
                apiErrorData(response.error)
            }
            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                //logic goes here(loops, if else, data manipulation)
                prefHelper.setUserName(response.value.data?.name)
                println("Access_Token - ${response.value.data?.accessToken}")
                prefHelper.setAccessToken(response.value.data?.accessToken)
                prefHelper.setMarketerMobile(response.value.data?.mobile)
                prefHelper.setMarketerCode(response.value.data?.marketerCode)
                prefHelper.setAccountId(response.value.data?.accountId)
                withContext(Dispatchers.Main) {
                    hideProgressBar()
                    _verifyOtpData.postValue(response.value)
                }
            }
        }
    }

    fun resendOTP(mobileNo: String) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Resnding OTP\nPlease wait..."))
        when (val response = repository.resendOTP(mobileNo)) {
            is ResultWrapper.Failure -> {
                apiErrorData(response.error)
            }
            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                withContext(Dispatchers.Main) {
                    hideProgressBar()
                    _resendOtpData.postValue(response.value)
                }
            }
        }
    }

    fun logoutApi() = viewModelScope.launch {
        showProgressBar(ProgressConfig("Logout\nPlease wait..."))
        when (val response = repository.logout(prefHelper.getMarketerMobile().toString())) {
            is ResultWrapper.Failure -> {
                apiErrorData(response.error)
               }
            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                withContext(Dispatchers.Main) {
                    hideProgressBar()
                    _logoutData.postValue(response.value)
                    prefHelper.logout()
                }
            }
        }
    }

    fun autoLogout() = viewModelScope.launch {
        showProgressBar(ProgressConfig("Checking Login Status\nPlease wait..."))
        when (val response = repository.autoLogout()) {
            is ResultWrapper.Failure -> {
                //_loginStatus.postValue(false)
                hideProgressBar()
            }
            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                withContext(Dispatchers.Main) {
                    hideProgressBar()
                    _loginStatus.postValue(response.value.data?.loginStatus)
                }
            }
        }
    }

    fun getCheckInStatus() = viewModelScope.launch{

        when( val response = repository.customerList()) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                prefHelper.setCheckinStatus(response.value.checkinStatus ?: false)
            }
        }


    }
}