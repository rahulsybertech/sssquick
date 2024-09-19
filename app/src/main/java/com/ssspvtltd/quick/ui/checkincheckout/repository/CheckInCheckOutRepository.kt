package com.ssspvtltd.quick.ui.checkincheckout.repository

import com.ssspvtltd.quick.model.auth.LoginData
import com.ssspvtltd.quick.model.checkincheckout.CheckInRequest
import com.ssspvtltd.quick.model.checkincheckout.CustomerData
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ApiService
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
import com.ssspvtltd.quick.utils.getDeviceUniqId
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