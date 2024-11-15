package com.ssspvtltd.quick.base

import com.ssspvtltd.quick.model.auth.AutoLogout
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ApiService
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
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
