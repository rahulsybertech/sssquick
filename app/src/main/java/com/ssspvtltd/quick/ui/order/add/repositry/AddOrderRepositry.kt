package com.ssspvtltd.quick.ui.order.add.repositry

import com.ssspvtltd.quick.model.order.add.ItemsData
import com.ssspvtltd.quick.model.order.add.PackTypeData
import com.ssspvtltd.quick.model.order.add.PurchasePartyData
import com.ssspvtltd.quick.model.order.add.SalepartyData
import com.ssspvtltd.quick.model.order.add.SchemeData
import com.ssspvtltd.quick.model.order.add.VoucherData
import com.ssspvtltd.quick.model.order.add.editorder.EditOrderData
import com.ssspvtltd.quick.model.order.add.salepartydetails.AllStation
import com.ssspvtltd.quick.model.order.add.salepartydetails.Data
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ApiService
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AddOrderRepositry @Inject constructor(private val apiService: ApiService){
    suspend fun getVoucher(): ResultWrapper<ApiResponse<*>, ApiResponse<VoucherData>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.VOUCHER_DATA.ordinal) {
            apiService.voucherData()
        }
    }
    suspend fun salePartyList(): ResultWrapper<ApiResponse<*>, ApiResponse<List<SalepartyData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.SALE_PARTY.ordinal) {
            apiService.saleParty()
        }
    }

    suspend fun allStationList(salePartyId:String,subPartyId:String): ResultWrapper<ApiResponse<*>, ApiResponse<List<AllStation>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.All_STATION.ordinal) {
            apiService.allStation(salePartyId,subPartyId)
        }
    }

    suspend fun salePartyDetail(accountId:String): ResultWrapper<ApiResponse<*>, ApiResponse<Data>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.SALE_PARTY_DETAILS.ordinal) {
            apiService.getSalePartyDetails(accountId)
        }
    }

    suspend fun scheme(): ResultWrapper<ApiResponse<*>, ApiResponse<List<SchemeData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.SCHEME.ordinal) {
            apiService.schemeList()
        }
    }

    suspend fun purchasePartyList(schemeId:String?): ResultWrapper<ApiResponse<*>, ApiResponse<List<PurchasePartyData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.PURCHASE_PARTY.ordinal) {
            apiService.purchasePartyList(schemeId)
        }
    }

    suspend fun itemList(purchasePartyId:String): ResultWrapper<ApiResponse<*>, ApiResponse<List<ItemsData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.ITEM_LIST.ordinal) {
            apiService.itemList(purchasePartyId)
        }
    }

    suspend fun packType(): ResultWrapper<ApiResponse<*>, ApiResponse<List<PackTypeData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.PACK_TYPE.ordinal) {
            apiService.packingType()
        }
    }

    suspend fun placeOrder(
        params: HashMap<String, RequestBody?>,
        documents: List<MultipartBody.Part>?
    ): ResultWrapper<ApiResponse<*>, ApiResponse<*>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.SAVE_ORDER.ordinal) {
            apiService.saveOrder(params, documents)
        }
    }

    suspend fun editOrder(orderId:String): ResultWrapper<ApiResponse<*>, ApiResponse<EditOrderData>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.EDIT_ORDER.ordinal) {
            apiService.getEditOrderData(orderId)
        }
    }
}