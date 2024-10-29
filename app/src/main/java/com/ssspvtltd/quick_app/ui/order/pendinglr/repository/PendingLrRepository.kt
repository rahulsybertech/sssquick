package com.ssspvtltd.quick_app.ui.order.pendinglr.repository

import com.ssspvtltd.quick_app.model.GetStockInOfficeOrderDetailsRequest
import com.ssspvtltd.quick_app.model.order.pendinglr.PendingLrData
import com.ssspvtltd.quick_app.networking.ApiRequestCode
import com.ssspvtltd.quick_app.networking.ApiResponse
import com.ssspvtltd.quick_app.networking.ApiService
import com.ssspvtltd.quick_app.networking.ResultWrapper
import com.ssspvtltd.quick_app.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class PendingLrRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun pendingLrList(getStockInOfficeOrderDetailsRequest: GetStockInOfficeOrderDetailsRequest): ResultWrapper<ApiResponse<*>, ApiResponse<List<PendingLrData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.PENDING_LR.ordinal) {
            apiService.pendingLr(getStockInOfficeOrderDetailsRequest)
        }
    }
}