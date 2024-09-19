package com.ssspvtltd.quick.ui.order.goodsreturn.repository

import com.ssspvtltd.quick.model.order.goodsreturn.GoodsReturnData
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ApiService
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class GoodsReturnRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getGoodsReturn(): ResultWrapper<ApiResponse<*>, ApiResponse<List<GoodsReturnData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.GOODS_RETURN.ordinal) {
            apiService.goodsReturn()
        }
    }
}