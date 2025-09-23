package com.ssspvtltd.quick.model.order.add.editorder


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class EditOrderDataNew(
    @SerializedName("bstationId")
    @Expose
    val bstationId: String?,
    @SerializedName("nickName")
    @Expose
    val nickName: String?,
    @SerializedName("bstationName")
    @Expose
    val bstationName: String?,
    @SerializedName("deliveryDateFrom")
    @Expose
    val deliveryDateFrom: String?,
    @SerializedName("deliveryDateTo")
    @Expose
    val deliveryDateTo: String?,
    @SerializedName("docsList")
    @Expose
    val docsList: List<DocListData?>?,
    @SerializedName("id")
    @Expose
    val id: String?,
    @SerializedName("itemDetailsList")
    @Expose
    val itemDetailsList: List<ItemDetails?>?,
    @SerializedName("maketerName")
    @Expose
    val maketerName: String?,
    @SerializedName("marketerCode")
    @Expose
    val marketerCode: String?,
    @SerializedName("marketerId")
    @Expose
    val marketerId: String?,
    @SerializedName("orderNo")
    @Expose
    val orderNo: String?,
    @SerializedName("orderTypeName")
    @Expose
    val orderCategary: String?,
    @SerializedName("ordercategary")
    @Expose
    val orderTypeName: String?,
    @SerializedName("orderStatus")
    @Expose
    val orderStatus: String?,
    @SerializedName("purchasePartyId")
    @Expose
    val purchasePartyId: String?,
    @SerializedName("purchasePartyName")
    @Expose
    val purchasePartyName: String?,
    @SerializedName("purchasePartyMobileNo")
    @Expose
    val purchasePartyMobileNo: String?,
    @SerializedName("pvtMarka")
    @Expose
    val pvtMarka: String?,
    @SerializedName("remark")
    @Expose
    val remark: String?,
    @SerializedName("salePartyId")
    @Expose
    val salePartyId: String?,
    @SerializedName("salePartyName")
    @Expose
    val salePartyName: String?,
    @SerializedName("schemeId")
    @Expose
    val schemeId: Any?,
    @SerializedName("schemeName")
    @Expose
    val schemeName: String?,
    @SerializedName("subPartyId")
    @Expose
    val subPartyId: Any?,


    @SerializedName("subPartyName")
    @Expose
    val subPartyName: String?,

    @SerializedName("dispatchType")
    @Expose
    val dispatchType: String?,

    @SerializedName("dispatchTypeID")
    @Expose
    val dispatchTypeID: String?,




    @SerializedName("subPartyasRemark")
    @Expose
    val subPartyasRemark: String?,
    @SerializedName("totalAmt")
    @Expose
    val totalAmt: Double?,
    @SerializedName("totalQty")
    @Expose
    val totalQty: Double?,
    @SerializedName("transportId")
    @Expose
    val transportId: String?,
    @SerializedName("transportName")
    @Expose
    val transportName: String?,


    @SerializedName("voucherCodeId")
    @Expose
    val voucherCodeId: String?,
    @SerializedName("voucherCodeNo")
    @Expose
    val voucherCodeNo: String?,
    @SerializedName("isCancel")
    @Expose
    val isCancel: Boolean?
) {
    data class DocListData(
        @SerializedName("docId")
        @Expose
        val docId: String?,
        @SerializedName("docsUrls")
        @Expose
        val docsUrls: String?,
    )
    data class ItemDetails(
        @SerializedName("id")
        @Expose
        val id: String?,
        @SerializedName("amount")
        @Expose
        val amount: Double?,
        @SerializedName("itemDetail")
        @Expose
        val itemDetail: List<ItemDetail?>?,
        @SerializedName("packName")
        @Expose
        val packName: String?,
        @SerializedName("pcsId")
        @Expose
        val pcsId: String?,
        @SerializedName("pcsType")
        @Expose
        val pcsType: Any?,
        @SerializedName("pcsTypeName")
        @Expose
        val pcsTypeName: Any?,
        @SerializedName("qty")
        @Expose
        val qty: Double?
    ) {
        
        data class ItemDetail(
            @SerializedName("amount")
            @Expose
            val amount: Double?,
            @SerializedName("colorName")
            @Expose
            val colorName: String?,
            @SerializedName("id")
            @Expose
            val id: String?,
            @SerializedName("itemId")
            @Expose
            val itemId: String?,
            @SerializedName("itemName")
            @Expose
            val itemName: String?,
            @SerializedName("itemQuantity")
            @Expose
            val itemQuantity: Double?,
            @SerializedName("sizeName")
            @Expose
            val sizeName: String?
        )
    }
}