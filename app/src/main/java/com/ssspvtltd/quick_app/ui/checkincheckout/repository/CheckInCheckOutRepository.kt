package com.ssspvtltd.quick_app.ui.checkincheckout.repository

import com.ssspvtltd.quick_app.model.checkincheckout.CheckInRequest
import com.ssspvtltd.quick_app.model.checkincheckout.CustomerData
import com.ssspvtltd.quick_app.networking.ApiRequestCode
import com.ssspvtltd.quick_app.networking.ApiResponse
import com.ssspvtltd.quick_app.networking.ApiService
import com.ssspvtltd.quick_app.networking.ResultWrapper
import com.ssspvtltd.quick_app.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CheckInCheckOutRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun customerList(): ResultWrapper<ApiResponse<*>, ApiResponse<List<CustomerData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.CUSTOMER_LIST.ordinal) {
            apiService.customer()
        }
    }

    suspend fun addUpdateCustomerList(checkInRequest: CheckInRequest?): ResultWrapper<ApiResponse<*>, ApiResponse<CheckInRequest?>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.ADD_UPDATE_CUSTOMER_LIST.ordinal) {
            apiService.addUpdateCustomer1(checkInRequest)
        }
    }

    suspend fun checkOutCustomer(): ResultWrapper<ApiResponse<*>, ApiResponse<*>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.CHECKOUT_CUSTOMER.ordinal) {
            apiService.checkOutCustomer()
        }
    }
}