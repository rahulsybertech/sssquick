package com.ssspvtltd.quick_new.ui.order.stockinoffice.repository

import com.ssspvtltd.quick_new.model.GetStockInOfficeOrderDetailsRequest
import com.ssspvtltd.quick_new.model.order.stockinoffice.StockInOfficeData
import com.ssspvtltd.quick_new.networking.ApiRequestCode
import com.ssspvtltd.quick_new.networking.ApiResponse
import com.ssspvtltd.quick_new.networking.ApiService
import com.ssspvtltd.quick_new.networking.ResultWrapper
import com.ssspvtltd.quick_new.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class StockInOfficeRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun stockInOffice(getStockInOfficeOrderDetailsRequest: GetStockInOfficeOrderDetailsRequest): ResultWrapper<ApiResponse<*>, ApiResponse<List<StockInOfficeData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.STOCK_IN_OFFICE.ordinal) {
            apiService.stockInOffice(getStockInOfficeOrderDetailsRequest)
        }
    }
}