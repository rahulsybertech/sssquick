package com.ssspvtltd.quick_app.model.order.editorder

import com.ssspvtltd.quick_app.model.order.add.additem.PackType

data class EditOrderData(
    val bstationId: String,
    val bstationName: String,
    val deliveryDateFrom: String,
    val deliveryDateTo: String,
    val docsList: List<Any>,
    val id: String,
    val itemDetailsList: List<PackType>,
    val maketerName: String,
    val marketerCode: String,
    val marketerId: String,
    val orderNo: String,
    val orderTypeName: String,
    val purchasePartyId: String,
    val purchasePartyName: String,
    val pvtMarka: String,
    val remark: String,
    val salePartyId: String,
    val salePartyName: String,
    val schemeId: String,
    val schemeName: String,
    val subPartyId: Any,
    val subPartyName: String,
    val subPartyasRemark: String,
    val totalAmt: Int,
    val totalQty: Int,
    val transportId: String,
    val transportName: String,
    val voucherCodeId: String,
    val voucherCodeNo: String
)