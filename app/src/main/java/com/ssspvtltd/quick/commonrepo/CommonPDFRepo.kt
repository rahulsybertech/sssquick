package com.ssspvtltd.quick.commonrepo

import com.ssspvtltd.quick.model.order.pending.PendingOrderPDFRegenerateRequest
import com.ssspvtltd.quick.model.order.pending.PendingOrderPDFRegenerateResponse
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ApiService
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import javax.inject.Inject

class CommonPDFRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun callOrderBookGeneratePdf(orderId: PendingOrderPDFRegenerateRequest.PendingOrderPDFRegenerateRequestItem) : ResultWrapper<ApiResponse<*>, Response<PendingOrderPDFRegenerateResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.ORDER_PDF_REGENERATE.ordinal) {
            apiService.getOrderBookGeneratePdf(orderId)
        }

    }

}