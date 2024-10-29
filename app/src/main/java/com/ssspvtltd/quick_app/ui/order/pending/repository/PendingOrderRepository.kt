package com.ssspvtltd.quick_app.ui.order.pending.repository

import com.ssspvtltd.quick_app.model.order.pending.FilterRequest
import com.ssspvtltd.quick_app.model.order.pending.PendingOrderData
import com.ssspvtltd.quick_app.networking.ApiRequestCode
import com.ssspvtltd.quick_app.networking.ApiResponse
import com.ssspvtltd.quick_app.networking.ApiService
import com.ssspvtltd.quick_app.networking.ResultWrapper
import com.ssspvtltd.quick_app.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class PendingOrderRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun pendingOrderList(filterRequest: FilterRequest): ResultWrapper<ApiResponse<*>, ApiResponse<List<PendingOrderData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.PENDING_ORDER.ordinal) {
            apiService.pendingOrder(filterRequest)
        }
    }

}