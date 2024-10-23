package com.ssspvtltd.quick_new.ui.order.pending.repository

import com.ssspvtltd.quick_new.model.order.pending.FilterRequest
import com.ssspvtltd.quick_new.model.order.pending.PendingOrderData
import com.ssspvtltd.quick_new.networking.ApiRequestCode
import com.ssspvtltd.quick_new.networking.ApiResponse
import com.ssspvtltd.quick_new.networking.ApiService
import com.ssspvtltd.quick_new.networking.ResultWrapper
import com.ssspvtltd.quick_new.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class PendingOrderRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun pendingOrderList(filterRequest: FilterRequest): ResultWrapper<ApiResponse<*>, ApiResponse<List<PendingOrderData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.PENDING_ORDER.ordinal) {
            apiService.pendingOrder(filterRequest)
        }
    }

}