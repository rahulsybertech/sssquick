package com.ssspvtltd.quick_new.ui.order.hold.repository

import com.ssspvtltd.quick_new.model.HoldOrderRequest
import com.ssspvtltd.quick_new.model.order.hold.HoldOrderData
import com.ssspvtltd.quick_new.networking.ApiRequestCode
import com.ssspvtltd.quick_new.networking.ApiResponse
import com.ssspvtltd.quick_new.networking.ApiService
import com.ssspvtltd.quick_new.networking.ResultWrapper
import com.ssspvtltd.quick_new.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class HoldOrderRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun holdOrderList(holdOrderRequest: HoldOrderRequest): ResultWrapper<ApiResponse<*>, ApiResponse<List<HoldOrderData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.HOLD_ORDER.ordinal) {
            apiService.holdOrder(holdOrderRequest = holdOrderRequest)
        }
    }
}