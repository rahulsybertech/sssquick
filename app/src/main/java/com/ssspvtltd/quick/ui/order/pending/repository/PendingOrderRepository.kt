package com.ssspvtltd.quick.ui.order.pending.repository

import com.ssspvtltd.quick.model.GetStockInOfficeOrderDetailsRequest
import com.ssspvtltd.quick.model.checkincheckout.CustomerData
import com.ssspvtltd.quick.model.order.pending.FilterRequest
import com.ssspvtltd.quick.model.order.pending.PendingOrderData
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ApiService
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class PendingOrderRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun pendingOrderList(filterRequest: FilterRequest): ResultWrapper<ApiResponse<*>, ApiResponse<List<PendingOrderData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.PENDING_ORDER.ordinal) {
            apiService.pendingOrder(filterRequest)
        }
    }
    suspend fun pendingOrderListByCustomer(customerID:String,pageNumber:String,pageSize:String): ResultWrapper<ApiResponse<*>, ApiResponse<List<PendingOrderData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.PENDING_ORDER.ordinal) {
            apiService.pendingOrderByCustomer(customerID,pageNumber,pageSize)
        }
    }

    suspend fun count(getStockInOfficeOrderDetailsRequest: GetStockInOfficeOrderDetailsRequest): ResultWrapper<ApiResponse<*>, ApiResponse<*>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.COUNT.ordinal) {
            apiService.count(getStockInOfficeOrderDetailsRequest)
        }
    }
    suspend fun countCustomer(id: String): ResultWrapper<ApiResponse<*>, ApiResponse<*>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.COUNT.ordinal) {
            apiService.countCustomer(id)
        }
    }

    suspend fun customerList(): ResultWrapper<ApiResponse<*>, ApiResponse<List<CustomerData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.CUSTOMER_LIST.ordinal) {
            apiService.customer()
        }
    }

}