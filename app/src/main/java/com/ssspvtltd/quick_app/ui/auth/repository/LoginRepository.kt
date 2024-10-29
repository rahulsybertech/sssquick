package com.ssspvtltd.quick_app.ui.auth.repository

import com.ssspvtltd.quick_app.model.auth.AutoLogout
import com.ssspvtltd.quick_app.model.auth.LoginData
import com.ssspvtltd.quick_app.model.auth.VerifyOtpData
import com.ssspvtltd.quick_app.networking.ApiRequestCode
import com.ssspvtltd.quick_app.networking.ApiResponse
import com.ssspvtltd.quick_app.networking.ApiService
import com.ssspvtltd.quick_app.networking.ResultWrapper
import com.ssspvtltd.quick_app.networking.safeApiCall
import com.ssspvtltd.quick_app.utils.getDeviceUniqId
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
}