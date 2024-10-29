package com.ssspvtltd.quick_app.ui.order.add.repositry

import com.ssspvtltd.quick_app.model.DashBoardDataResponse
import com.ssspvtltd.quick_app.networking.ApiRequestCode
import com.ssspvtltd.quick_app.networking.ApiResponse
import com.ssspvtltd.quick_app.networking.ApiService
import com.ssspvtltd.quick_app.networking.ResultWrapper
import com.ssspvtltd.quick_app.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import javax.inject.Inject

class DashBoardRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun callGetDashBoardDetails(marketerId: String) : ResultWrapper<ApiResponse<*>, Response<DashBoardDataResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_DATA.ordinal) {
            apiService.getDashBoardData(marketerId = marketerId)
        }

    }
    suspend fun callGetDashBoardSaleCountDetails(marketerId: String, fromDate: String, toDate: String) : ResultWrapper<ApiResponse<*>, Response<DashBoardDataResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_SALE_DATA.ordinal) {
            apiService.getDashBoardSaleCountData(marketerId = marketerId, fromDate = fromDate, toDate = toDate)
        }

    }
}