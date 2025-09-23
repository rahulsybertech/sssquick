package com.ssspvtltd.quick.ui.order.goodsreturn.repository

import com.ssspvtltd.quick.model.GetStockInOfficeOrderDetailsRequest
import com.ssspvtltd.quick.model.GoodsReturnImageUploadRequest
import com.ssspvtltd.quick.model.gr.GoodsReturnDataGr
import com.ssspvtltd.quick.model.order.goodsreturn.GoodsReturnData
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ApiService
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class GoodsReturnRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getGoodsReturn(getStockInOfficeOrderDetailsRequest: GetStockInOfficeOrderDetailsRequest): ResultWrapper<ApiResponse<*>, ApiResponse<List<GoodsReturnData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.GOODS_RETURN.ordinal) {
            apiService.goodsReturn(getStockInOfficeOrderDetailsRequest)
        }
    }
    suspend fun getGoodsReturnSecondary(getStockInOfficeOrderDetailsRequest: GetStockInOfficeOrderDetailsRequest): ResultWrapper<ApiResponse<*>, ApiResponse<List<GoodsReturnData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.GOODS_RETURN.ordinal) {
            apiService.goodsReturnSecondary(getStockInOfficeOrderDetailsRequest)
        }
    }

    suspend fun uploadImages(request: GoodsReturnImageUploadRequest): ResultWrapper<ApiResponse<*>, ApiResponse<*>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.UPLOAD_IMAGES.ordinal) {
            apiService.uploadGoodsReturnImages(request)
        }
    }

    suspend fun editOrderGr(orderId:String): ResultWrapper<ApiResponse<*>, ApiResponse<GoodsReturnDataGr>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.EDIT_ORDER.ordinal) {
            apiService.getEditOrderDataGr(orderId)
        }
    }
}