package com.ssspvtltd.quick_app.base

import com.ssspvtltd.quick_app.model.auth.AutoLogout
import com.ssspvtltd.quick_app.networking.ApiRequestCode
import com.ssspvtltd.quick_app.networking.ApiResponse
import com.ssspvtltd.quick_app.networking.ApiService
import com.ssspvtltd.quick_app.networking.ResultWrapper
import com.ssspvtltd.quick_app.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class BaseRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun autoLogout(): ResultWrapper<ApiResponse<*>, ApiResponse<AutoLogout>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.LOGOUT_STATUS.ordinal) {
            apiService.autoLogout()
        }
    }

    suspend fun logout(mobileNo: String): ResultWrapper<ApiResponse<*>, ApiResponse<*>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.LOGOUT.ordinal) {
            apiService.logout(mobileNo)
        }
    }
}
