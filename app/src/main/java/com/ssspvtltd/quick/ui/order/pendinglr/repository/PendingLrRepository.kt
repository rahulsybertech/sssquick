package com.ssspvtltd.quick.ui.order.pendinglr.repository

import com.google.gson.JsonObject
import com.ssspvtltd.quick.model.GetStockInOfficeOrderDetailsRequest
import com.ssspvtltd.quick.model.IntResponse
import com.ssspvtltd.quick.model.auth.LoginData
import com.ssspvtltd.quick.model.order.pendinglr.PendingLrData
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ApiService
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
import com.ssspvtltd.quick.utils.getDeviceUniqId
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class PendingLrRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun pendingLrList(getStockInOfficeOrderDetailsRequest: GetStockInOfficeOrderDetailsRequest): ResultWrapper<ApiResponse<*>, ApiResponse<List<PendingLrData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.PENDING_LR.ordinal) {
            apiService.pendingLr(getStockInOfficeOrderDetailsRequest)
        }
    }


    suspend fun count(getStockInOfficeOrderDetailsRequest: GetStockInOfficeOrderDetailsRequest): ResultWrapper<ApiResponse<*>, ApiResponse<*>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.COUNT.ordinal) {
            apiService.countPendingLR(getStockInOfficeOrderDetailsRequest)
        }
    }





}