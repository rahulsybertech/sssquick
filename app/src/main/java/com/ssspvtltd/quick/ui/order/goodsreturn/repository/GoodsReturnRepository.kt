package com.ssspvtltd.quick.ui.order.goodsreturn.repository
import com.google.gson.JsonObject
import com.ssspvtltd.quick.model.customer.AccountNameResponse
import com.ssspvtltd.quick.model.GetStockInOfficeOrderDetailsRequest
import com.ssspvtltd.quick.model.GoodsReturnImageUploadRequest
import com.ssspvtltd.quick.model.customer.NickNameResponse
import com.ssspvtltd.quick.model.editCustomer.EditCustomerDetailsResponse
import com.ssspvtltd.quick.model.gr.GoodsReturnDataGr
import com.ssspvtltd.quick.model.mailbox.MailData
import com.ssspvtltd.quick.model.order.goodsreturn.GoodsReturnData
import com.ssspvtltd.quick.model.order.pending.PendingOrderPDFRegenerateRequest
import com.ssspvtltd.quick.model.order.pending.PendingOrderPDFRegenerateResponse
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ApiService
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
import com.ssspvtltd.quick.ui.customerDetails.model.CreateResponse
import com.ssspvtltd.quick.ui.customerDetails.modelRequest.CustomerDetailsRequest
import com.ssspvtltd.quick.ui.customerDetails.modelRequest.DeleteAccountRequest
import com.ssspvtltd.quick.ui.customerDetails.modelRequest.DeleteLeadRequest
import com.ssspvtltd.quick.ui.tour.model.LeadDetailsByLeadIdRespnse
import com.ssspvtltd.quick.ui.tour.model.LeadResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
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
    suspend fun getMailBox(getStockInOfficeOrderDetailsRequest: GetStockInOfficeOrderDetailsRequest): ResultWrapper<ApiResponse<*>, ApiResponse<List<MailData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.MAIL_BOX.ordinal) {
            apiService.mailBoxApi(getStockInOfficeOrderDetailsRequest)
        }
    }
   /* suspend fun getAccountNameListt(): ResultWrapper<ApiResponse<*>, ApiResponse<List<AccountNameResponse>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.MAIL_BOX.ordinal) {
            apiService.bankAccountName()
        }
    }*/
    suspend fun getAccountNameListt(): ResultWrapper<ApiResponse<*>, Response<AccountNameResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_DATA.ordinal) {
            apiService.bankAccountName()
        }}

    suspend fun getNickNameList(): ResultWrapper<ApiResponse<*>, Response<NickNameResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_DATA.ordinal) {
            apiService.nickNameApi()
        }}

    suspend fun customerNameByNickName(id: String): ResultWrapper<ApiResponse<*>, Response<AccountNameResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_DATA.ordinal) {
            apiService.customerNameByNickNameApi(id)
        }
    }
    suspend fun addCustomerDetails(customerDetailsRequest: CustomerDetailsRequest) : ResultWrapper<ApiResponse<*>, Response<AccountNameResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.ORDER_PDF_REGENERATE.ordinal) {
            apiService.addCustomerApi(customerDetailsRequest)
        }
    }


    suspend fun allAccountReq(): ResultWrapper<ApiResponse<*>, Response<AccountNameResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_DATA.ordinal) {
            apiService.allAccountApi()
        }}

    suspend fun deleteAccountForIDReq(id: DeleteAccountRequest) : ResultWrapper<ApiResponse<*>, Response<AccountNameResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.ORDER_PDF_REGENERATE.ordinal) {
            apiService.deleteAccountForIDApi(id)
        }
    }

    suspend fun accountDetailsForIdReq(id: JsonObject) : ResultWrapper<ApiResponse<*>, Response<EditCustomerDetailsResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.ORDER_PDF_REGENERATE.ordinal) {
            apiService.accountDetailsForIDApi(id)
        }
    }

    suspend fun customerListByCustomerCodeReq(searchValue: String): ResultWrapper<ApiResponse<*>, Response<CreateResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_DATA.ordinal) {
            apiService.getAllBranchFairAccounts(searchValue)
        }}

    suspend fun getAllLeadReq(): ResultWrapper<ApiResponse<*>, Response<LeadResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DASHBOARD_DATA.ordinal) {
            apiService.getAllLeadApi()
        }}

    suspend fun deleteLeadForIDReq(id: DeleteLeadRequest) : ResultWrapper<ApiResponse<*>, Response<AccountNameResponse>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.ORDER_PDF_REGENERATE.ordinal) {
            apiService.deleteLeadForIDApi(id.leadID)
        }
    }


}