package com.ssspvtltd.quick_new.ui.order.goodsreturn.repository

import com.ssspvtltd.quick_new.model.GetStockInOfficeOrderDetailsRequest
import com.ssspvtltd.quick_new.model.order.goodsreturn.GoodsReturnData
import com.ssspvtltd.quick_new.networking.ApiRequestCode
import com.ssspvtltd.quick_new.networking.ApiResponse
import com.ssspvtltd.quick_new.networking.ApiService
import com.ssspvtltd.quick_new.networking.ResultWrapper
import com.ssspvtltd.quick_new.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class GoodsReturnRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getGoodsReturn(getStockInOfficeOrderDetailsRequest: GetStockInOfficeOrderDetailsRequest): ResultWrapper<ApiResponse<*>, ApiResponse<List<GoodsReturnData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.GOODS_RETURN.ordinal) {
            apiService.goodsReturn(getStockInOfficeOrderDetailsRequest)
        }
    }
}