package com.ssspvtltd.quick_new.ui.order.pendinglr.repository

import com.ssspvtltd.quick_new.model.order.pendinglr.PendingLrData
import com.ssspvtltd.quick_new.networking.ApiRequestCode
import com.ssspvtltd.quick_new.networking.ApiResponse
import com.ssspvtltd.quick_new.networking.ApiService
import com.ssspvtltd.quick_new.networking.ResultWrapper
import com.ssspvtltd.quick_new.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class PendingLrRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun pendingLrList(): ResultWrapper<ApiResponse<*>, ApiResponse<List<PendingLrData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.PENDING_LR.ordinal) {
            apiService.pendingLr()
        }
    }
}