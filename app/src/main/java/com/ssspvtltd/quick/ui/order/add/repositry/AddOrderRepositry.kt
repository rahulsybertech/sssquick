package com.ssspvtltd.quick.ui.order.add.repositry

import com.ssspvtltd.quick.model.SubPartyGRData
import com.ssspvtltd.quick.model.SubPartyGRResponse
import com.ssspvtltd.quick.model.TransportMasterData
import com.ssspvtltd.quick.model.checkincheckout.CustomerData
import com.ssspvtltd.quick.model.gr.GoodsReturnDataGr
import com.ssspvtltd.quick.model.order.add.DispatchTypeList
import com.ssspvtltd.quick.model.order.add.DispatchTypeResponse
import com.ssspvtltd.quick.model.order.add.ItemsData
import com.ssspvtltd.quick.model.order.add.PackTypeData
import com.ssspvtltd.quick.model.order.add.PurchasePartyData
import com.ssspvtltd.quick.model.order.add.SalepartyData
import com.ssspvtltd.quick.model.order.add.SchemeData
import com.ssspvtltd.quick.model.order.add.VoucherData
import com.ssspvtltd.quick.model.order.add.editorder.EditOrderDataNew
import com.ssspvtltd.quick.model.order.add.salepartydetails.AllStation
import com.ssspvtltd.quick.model.order.add.salepartydetails.Data
import com.ssspvtltd.quick.model.order.goodsreturn.GoodsReturnData
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
    suspend fun customerList(): ResultWrapper<ApiResponse<*>, ApiResponse<List<CustomerData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.CUSTOMER_LIST.ordinal) {
            apiService.customer()
        }
    }
    suspend fun salePartyList(): ResultWrapper<ApiResponse<*>, ApiResponse<List<SalepartyData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.SALE_PARTY.ordinal) {
            apiService.saleParty()
        }
    }
 suspend fun getGetDispatchTypeListList(): ResultWrapper<ApiResponse<*>, ApiResponse<List<DispatchTypeList>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.DispatchType.ordinal) {
            apiService.dispatchType()
        }
    }

   /* suspend fun allStationList(salePartyId:String,subPartyId:String): ResultWrapper<ApiResponse<*>, ApiResponse<List<AllStation>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.All_STATION.ordinal) {
            apiService.allStation(salePartyId,subPartyId)
        }
    }*/

    suspend fun salePartyDetail(accountId:String): ResultWrapper<ApiResponse<*>, ApiResponse<Data>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.SALE_PARTY_DETAILS.ordinal) {
            apiService.getSalePartyDetails(accountId)
        }
    }
    suspend fun subPartyDetailGR(accountId:String): ResultWrapper<ApiResponse<*>, ApiResponse<List<SubPartyGRData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.SALE_PARTY_DETAILS.ordinal) {
            apiService.getSubPartyDetailsGR(accountId)
        }
    }
    suspend fun tranportDetailsGRReq(accountId:String): ResultWrapper<ApiResponse<*>, ApiResponse<List<TransportMasterData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.SALE_PARTY_DETAILS.ordinal) {
            apiService.getTransportGRApi(accountId)
        }
    }
    suspend fun salePartyDetailNew(accountId:String): ResultWrapper<ApiResponse<*>, ApiResponse<com.ssspvtltd.quick.model.order.add.salepartyNewList.Data>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.SALE_PARTY_DETAILS.ordinal) {
            apiService.getSalePartyDetailsNew(accountId)
        }
    }

    suspend fun scheme(): ResultWrapper<ApiResponse<*>, ApiResponse<List<SchemeData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.SCHEME.ordinal) {
            apiService.schemeList()
        }
    }

    suspend fun purchasePartyList(nickNameId :String?,schemeId:String?, type : Boolean): ResultWrapper<ApiResponse<*>, ApiResponse<List<PurchasePartyData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.PURCHASE_PARTY.ordinal) {
            apiService.purchasePartyList(nickNameId,schemeId, type)
        }
    }
    suspend fun purchasePartyListInGr(nickNameId :String?,schemeId:String?, type : Boolean): ResultWrapper<ApiResponse<*>, ApiResponse<List<PurchasePartyData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.PURCHASE_PARTY.ordinal) {
            apiService.purchasePartyLisInGr(nickNameId,schemeId, true)
        }
    }
    suspend fun purchasePartyListWithSuplier(nickNameId :String?,schemeId:String?, type : Boolean): ResultWrapper<ApiResponse<*>, ApiResponse<List<PurchasePartyData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.NICK_NAME_LIST.ordinal) {
            apiService.purchasePartyList(nickNameId,schemeId, type)
        }
    }
    suspend fun purchasePartyListWithNichName(schemeId:String?, type : Boolean): ResultWrapper<ApiResponse<*>, ApiResponse<List<PurchasePartyData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.PURCHASE_PARTY.ordinal) {
            apiService.purchasePartyListWithNickName(schemeId, type)
        }
    }
    suspend fun purchasePartyListByNickName(nickNameId:String?): ResultWrapper<ApiResponse<*>, ApiResponse<List<PurchasePartyData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.PURCHASE_PARTY_WITH_NICKNAME.ordinal) {
            apiService.purchasePartyListByNickName(nickNameId)
        }
    }

    suspend fun itemList(purchasePartyId:String): ResultWrapper<ApiResponse<*>, ApiResponse<List<ItemsData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.ITEM_LIST.ordinal) {
            apiService.itemList(purchasePartyId)
        }
    }

    suspend fun itemListGr(purchasePartyId:String): ResultWrapper<ApiResponse<*>, ApiResponse<List<ItemsData>>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.ITEM_LIST.ordinal) {
            apiService.itemListGr()
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

    suspend fun placeOrderGr(
        params: HashMap<String, RequestBody?>,
        documents: List<MultipartBody.Part>?
    ): ResultWrapper<ApiResponse<*>, ApiResponse<*>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.SAVE_ORDER.ordinal) {
            apiService.saveOrderGr(params, documents)
        }
    }

    suspend fun editOrder(orderId:String): ResultWrapper<ApiResponse<*>, ApiResponse<EditOrderDataNew>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.EDIT_ORDER.ordinal) {
            apiService.getEditOrderData(orderId)
        }
    }
    suspend fun editOrderGr(orderId:String): ResultWrapper<ApiResponse<*>, ApiResponse<GoodsReturnDataGr>> {
        return safeApiCall(Dispatchers.IO, ApiRequestCode.EDIT_ORDER.ordinal) {
            apiService.getEditOrderDataGr(orderId)
        }
    }
}