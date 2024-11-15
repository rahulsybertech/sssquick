package com.ssspvtltd.quick.ui.order.stockinoffice.repository

import com.ssspvtltd.quick.model.GetStockInOfficeOrderDetailsRequest
import com.ssspvtltd.quick.model.order.stockinoffice.StockInOfficeData
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ApiService
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class StockInOfficeRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun stockInOffice(getStockInOfficeOrderDetailsRequest: GetStockInOfficeOrderDetailsRequest): ResultWrapper<ApiResponse<*>, ApiResponse<List<StockInOfficeData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.STOCK_IN_OFFICE.ordinal) {
            apiService.stockInOffice(getStockInOfficeOrderDetailsRequest)
        }
    }
}