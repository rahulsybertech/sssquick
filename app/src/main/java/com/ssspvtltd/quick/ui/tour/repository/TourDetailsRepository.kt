package com.ssspvtltd.quick.ui.tour.repository

import com.ssspvtltd.quick.model.customer.AccountNameResponse
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ApiService
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
import com.ssspvtltd.quick.ui.tour.model.CategoryResponse
import com.ssspvtltd.quick.ui.tour.model.CommonResponse
import com.ssspvtltd.quick.ui.tour.model.GetAccountDetailByIdResponse
import com.ssspvtltd.quick.ui.tour.model.GradeResponse
import com.ssspvtltd.quick.ui.tour.model.LeadDetailsByLeadIdRespnse
import com.ssspvtltd.quick.ui.tour.model.LeadRequest
import com.ssspvtltd.quick.ui.tour.model.ShopCategoryResponse
import com.ssspvtltd.quick.ui.tour.model.StateResponse
import com.ssspvtltd.quick.ui.tour.model.StationResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import javax.inject.Inject

class TourDetailsRepository @Inject constructor(private val apiService: ApiService) {
/*    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)
    @SuppressLint("MissingPermission")
    fun getLocationFlow(): Flow<Location> = callbackFlow {

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000
        ).setMaxUpdates(1).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    trySend(it)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }*/

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

    suspend fun getLeadByLeadIDReq(leadID: String): ResultWrapper<ApiResponse<*>, Response<LeadDetailsByLeadIdRespnse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_DATA.ordinal) {
            apiService.getLeadByLeadIDApi(leadID)
        }}

    suspend fun getAccountDetailsByIDReq(accountId: String?): ResultWrapper<ApiResponse<*>, Response<GetAccountDetailByIdResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_DATA.ordinal) {
            apiService.getAccountDetailsByIDReqApi(accountId)
        }}





}