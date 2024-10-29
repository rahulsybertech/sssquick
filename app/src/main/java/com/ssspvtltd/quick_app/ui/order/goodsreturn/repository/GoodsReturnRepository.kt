package com.ssspvtltd.quick_app.ui.order.goodsreturn.repository

import com.ssspvtltd.quick_app.model.GetStockInOfficeOrderDetailsRequest
import com.ssspvtltd.quick_app.model.order.goodsreturn.GoodsReturnData
import com.ssspvtltd.quick_app.networking.ApiRequestCode
import com.ssspvtltd.quick_app.networking.ApiResponse
import com.ssspvtltd.quick_app.networking.ApiService
import com.ssspvtltd.quick_app.networking.ResultWrapper
import com.ssspvtltd.quick_app.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class GoodsReturnRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getGoodsReturn(getStockInOfficeOrderDetailsRequest: GetStockInOfficeOrderDetailsRequest): ResultWrapper<ApiResponse<*>, ApiResponse<List<GoodsReturnData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.GOODS_RETURN.ordinal) {
            apiService.goodsReturn(getStockInOfficeOrderDetailsRequest)
        }
    }
}