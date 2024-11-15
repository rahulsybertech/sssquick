package com.ssspvtltd.quick.ui.order.hold.repository

import com.ssspvtltd.quick.model.HoldOrderRequest
import com.ssspvtltd.quick.model.order.hold.HoldOrderData
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ApiService
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class HoldOrderRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun holdOrderList(holdOrderRequest: HoldOrderRequest): ResultWrapper<ApiResponse<*>, ApiResponse<List<HoldOrderData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.HOLD_ORDER.ordinal) {
            apiService.holdOrder(holdOrderRequest = holdOrderRequest)
        }
    }
}