package com.ssspvtltd.quick.ui.order.add.repositry

import com.ssspvtltd.quick.model.DashBoardDataResponse
import com.ssspvtltd.quick.model.version.CheckVersionResponse
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ApiService
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import javax.inject.Inject

class DashBoardRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun callGetDashBoardDetails(marketerId: String): ResultWrapper<ApiResponse<*>, Response<DashBoardDataResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_DATA.ordinal) {
            apiService.getDashBoardData(marketerId = marketerId)
        }

    }

    suspend fun callGetDashBoardSaleCountDetails(
        marketerId: String,
        fromDate: String?,
        toDate: String?
    ): ResultWrapper<ApiResponse<*>, Response<DashBoardDataResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_SALE_DATA.ordinal) {
            apiService.getDashBoardSaleCountData(
                marketerId = marketerId,
                fromDate = fromDate,
                toDate = toDate
            )
        }

    }

    suspend fun checkVersion(appName: String): ResultWrapper<ApiResponse<*>, Response<CheckVersionResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.CHECK_VERSION.ordinal) {
            apiService.fetchVersion(appName)
        }
    }

}