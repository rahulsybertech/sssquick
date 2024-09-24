package com.ssspvtltd.quick.networking

import com.ssspvtltd.quick.model.HoldOrderRequest
import com.ssspvtltd.quick.model.auth.AutoLogout
import com.ssspvtltd.quick.model.auth.LoginData
import com.ssspvtltd.quick.model.auth.VerifyOtpData
import com.ssspvtltd.quick.model.checkincheckout.CheckInRequest
import com.ssspvtltd.quick.model.checkincheckout.CustomerData
import com.ssspvtltd.quick.model.order.add.ItemsData
import com.ssspvtltd.quick.model.order.add.PackTypeData
import com.ssspvtltd.quick.model.order.add.PurchasePartyData
import com.ssspvtltd.quick.model.order.add.SalepartyData
import com.ssspvtltd.quick.model.order.add.SchemeData
import com.ssspvtltd.quick.model.order.add.VoucherData
import com.ssspvtltd.quick.model.order.add.editorder.EditOrderData
import com.ssspvtltd.quick.model.order.add.salepartydetails.AllStation
import com.ssspvtltd.quick.model.order.add.salepartydetails.Data
import com.ssspvtltd.quick.model.order.goodsreturn.GoodsReturnData
import com.ssspvtltd.quick.model.order.hold.HoldOrderData
import com.ssspvtltd.quick.model.order.pending.FilterRequest
import com.ssspvtltd.quick.model.order.pending.PendingOrderData
import com.ssspvtltd.quick.model.order.pendinglr.PendingLrData
import com.ssspvtltd.quick.model.order.stockinoffice.StockInOfficeData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query


interface ApiService {

    @POST("api/Login/GetLoginDetails")
    suspend fun loginUser(
        @Query("mobileNo") mobileNo: String?,
        @Query("deviceID") deviceID: String?
    ): ApiResponse<LoginData>

    @POST("api/Login/CheckLoginStatus")
    suspend fun autoLogout(): ApiResponse<AutoLogout>


    @POST("api/Login/VerifyOTP")
    suspend fun verifyOTP(
        @Query("mobileNo") mobileNo: String?,
        @Query("otp") otp: String?,
        @Query("deviceID") deviceID: String?,
    ): ApiResponse<VerifyOtpData>

    @POST("api/Logout/LogOutUser")
    suspend fun logout(@Query("mobileNo") mobileNo: String?): ApiResponse<*>

    @POST("api/Login/ResendOTP")
    suspend fun resendOtp(@Query("mobileNo") mobileNo: String?): ApiResponse<*>

    @POST("api/CheckInCheckout/GetCustomerList")
    suspend fun customer(
    ): ApiResponse<List<CustomerData>>

    @POST("api/CheckInCheckout/AddUpdateCustomerList")
    suspend fun addUpdateCustomer(
        @Query("Address") address: String?,
        @Query("Latitude") lattitude: String?,
        @Query("Longitude") longitude: String?,
        @Query("Remarks") remarks: String?,
        @Query("customerList") customerList: String?,
    ): ApiResponse<*>

    @POST("api/CheckInCheckout/AddUpdateCustomerList")
    suspend fun addUpdateCustomer1(
        @Body checkInRequest: CheckInRequest?
    ): ApiResponse<CheckInRequest?>

    @POST("api/CheckInCheckout/CheckOutAndRemoveCustomers")
    suspend fun checkOutCustomer(
    ): ApiResponse<*>

    @POST("api/OrderBook/GetVoucherDetails")
    suspend fun voucherData(
    ): ApiResponse<VoucherData>

    @POST("api/OrderBook/GetSalePartyList")
    suspend fun saleParty(
    ): ApiResponse<List<SalepartyData>>

    @POST("api/OrderBook/GetSchemeName")
    suspend fun schemeList(
    ): ApiResponse<List<SchemeData>>

    @POST("api/OrderBook/GetPurchasePartyList")
    suspend fun purchasePartyList(
        @Query("schemeId") schemeId: String?
    ): ApiResponse<List<PurchasePartyData>>

    @POST("api/OrderBook/GetItemList")
    suspend fun itemList(
        @Query("purchasePartyId") purchasePartyId: String
    ): ApiResponse<List<ItemsData>>

    @POST("api/OrderBook/GetStationListByMainPartyId")
    suspend fun allStation(
        @Query("mainPartyId") salePartyId: String,
        @Query("subPartyId") subPartyId: String
    ): ApiResponse<List<AllStation>>

    @POST("api/OrderBook/GetPackTypeList")
    suspend fun packingType(
    ): ApiResponse<List<PackTypeData>>

    @POST("api/OrderBook/GetSalePartyDetail")
    suspend fun getSalePartyDetails(
        @Query("accountId") accountId: String
    ): ApiResponse<Data>

    @POST("api/Report/GetPendingOrderDetails")
    suspend fun pendingOrder(
        @Body filterRequest: FilterRequest?
    ): ApiResponse<List<PendingOrderData>>


    @POST("api/Report/GetHoldOrderDetails")
    suspend fun holdOrder(
       @Body holdOrderRequest: HoldOrderRequest?
    ): ApiResponse<List<HoldOrderData>>


    @POST("api/Report/GetGrReturnOrderDetails")
    suspend fun goodsReturn(
    ): ApiResponse<List<GoodsReturnData>>

    @POST("api/Report/GetPendingLROrderDetails")
    suspend fun pendingLr(
    ): ApiResponse<List<PendingLrData>>

    @POST("api/Report/GetStockInOfficeOrderDetails")
    suspend fun stockInOffice(
    ): ApiResponse<List<StockInOfficeData>>

    @Multipart
    @POST("api/OrderBook/AddUpdateOrderBook")
    suspend fun saveOrder(
        @PartMap params: HashMap<String, RequestBody?>,
        @Part documents: List<MultipartBody.Part>?
    ): ApiResponse<*>

    @POST("api/OrderBook/GetOrderBookDetailsById")
    suspend fun getEditOrderData(
        @Query("orderId") orderId: String
    ): ApiResponse<EditOrderData>
}