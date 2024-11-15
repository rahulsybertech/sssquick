package com.ssspvtltd.quick.ui.order.pendinglr.repository

import com.ssspvtltd.quick.model.GetStockInOfficeOrderDetailsRequest
import com.ssspvtltd.quick.model.order.pendinglr.PendingLrData
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ApiService
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class PendingLrRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun pendingLrList(getStockInOfficeOrderDetailsRequest: GetStockInOfficeOrderDetailsRequest): ResultWrapper<ApiResponse<*>, ApiResponse<List<PendingLrData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.PENDING_LR.ordinal) {
            apiService.pendingLr(getStockInOfficeOrderDetailsRequest)
        }
    }
}