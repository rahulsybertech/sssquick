package com.ssspvtltd.quick.model.grNew


data class GoodsReturnResponse(
    val data: List<GoodsReturnDataNew>?
)
data class GoodsReturnDataNew(
    val id: String?,
    val billDate: String,
    val saleBillNo: String?,
    val itemName: String?,
    val totalQty: Int?,
    val totalAmt: Double?,
    val salePartyName: String?,
    val supplierName: String?,
    val goodsReturnDetailList: List<GoodsReturnItem>,
    var isExpanded: Boolean = false
)

data class GoodsReturnItem(
    val amount: Int?,
    val changedQty: Int?,
    val id: String,
    val itemName: String?,
    val grInDate: String?,
    val noChangeQty: Int?,
    val qty: Int?,
    val remark: String?,
    val saleBillNo: String?,
    val salePartyEmail: String?,
    val salePartyMob: String?,
    val salePartyName: String?,
    val status: String?,
    val subPartyName: String?,
    val supplierMob: String?,
    val supplierName: String?
    // Add other fields as needed
)
