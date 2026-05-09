package com.ssspvtltd.quick.ui.tour.repository

import com.ssspvtltd.quick.model.GetStockInOfficeOrderDetailsRequest
import com.ssspvtltd.quick.model.customer.AccountNameResponse
import com.ssspvtltd.quick.model.order.goodsreturn.GoodsReturnData
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ApiService
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
import com.ssspvtltd.quick.ui.tour.model.CategoryResponse
import com.ssspvtltd.quick.ui.tour.model.CommonResponse
import com.ssspvtltd.quick.ui.tour.model.GradeResponse
import com.ssspvtltd.quick.ui.tour.model.LeadRequest
import com.ssspvtltd.quick.ui.tour.model.ShopCategoryItem
import com.ssspvtltd.quick.ui.tour.model.ShopCategoryResponse
import com.ssspvtltd.quick.ui.tour.model.StateResponse
import com.ssspvtltd.quick.ui.tour.model.StationItem
import com.ssspvtltd.quick.ui.tour.model.StationResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import javax.inject.Inject

class TourDetailsRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getAccountNameListt(): ResultWrapper<ApiResponse<*>, Response<AccountNameResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_DATA.ordinal) {
            apiService.frimListApii()
        }}


    suspend fun submitLeadd(rquest: LeadRequest): ResultWrapper<ApiResponse<*>, Response<CommonResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_DATA.ordinal) {
            apiService.submitLeadApi(rquest)
        }}

    suspend fun getgetGradeList(): ResultWrapper<ApiResponse<*>, Response<GradeResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_DATA.ordinal) {
            apiService.getGradeList()
        }}

    suspend fun getgetStationList(): ResultWrapper<ApiResponse<*>, Response<StationResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_DATA.ordinal) {
            apiService.getStationList()
        }
    }
    suspend fun getgetStateList(): ResultWrapper<ApiResponse<*>, Response<StateResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_DATA.ordinal) {
            apiService.getStateList()
        }
    }

    suspend fun getgetCategoryList(): ResultWrapper<ApiResponse<*>, Response<CategoryResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_DATA.ordinal) {
            apiService.getCategoryList()
        }
    }
    suspend fun getgetShopCategoryList(): ResultWrapper<ApiResponse<*>, Response<ShopCategoryResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_DATA.ordinal) {
            apiService.getShopCategoryList()
        }
    }

}