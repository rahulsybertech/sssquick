package com.ssspvtltd.quick.ui.auth.repository

import com.ssspvtltd.quick.model.auth.AutoLogout
import com.ssspvtltd.quick.model.auth.LoginData
import com.ssspvtltd.quick.model.auth.VerifyOtpData
import com.ssspvtltd.quick.model.checkincheckout.CustomerData
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ApiService
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
import com.ssspvtltd.quick.utils.getDeviceUniqId
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class LoginRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun loginUser(mobileNo: String?): ResultWrapper<ApiResponse<*>, ApiResponse<LoginData>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.LOGIN.ordinal) {
            apiService.loginUser(mobileNo, getDeviceUniqId())
        }
    }

    suspend fun verifyOTP(
        mobileNo: String?, otp: String?
    ): ResultWrapper<ApiResponse<*>, ApiResponse<VerifyOtpData>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.VERIFY_OTP.ordinal) {
            apiService.verifyOTP(mobileNo, otp, getDeviceUniqId())
        }
    }

    suspend fun resendOTP(mobileNo: String?): ResultWrapper<ApiResponse<*>, ApiResponse<*>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.RESEND_OTP.ordinal) {
            apiService.resendOtp(mobileNo)
        }
    }

    suspend fun logout(mobileNo: String): ResultWrapper<ApiResponse<*>, ApiResponse<*>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.LOGOUT.ordinal) {
            apiService.logout(mobileNo)
        }
    }


    suspend fun autoLogout(): ResultWrapper<ApiResponse<*>, ApiResponse<AutoLogout>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.LOGOUT_STATUS.ordinal) {
            apiService.autoLogout()
        }
    }

    suspend fun customerList(): ResultWrapper<ApiResponse<*>, ApiResponse<List<CustomerData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.CUSTOMER_LIST.ordinal) {
            apiService.customer()
        }
    }

}