package com.ssspvtltd.quick.model.order.add.editorder

import com.ssspvtltd.quick.model.order.add.additem.PackType

data class EditOrderData(
    var bstationId: String,
    var bstationName: String,
    var deliveryDateFrom: String,
    var deliveryDateTo: String,
    var docsList: List<Any>,
    var id: String,
    var itemDetailsList: List<PackType>,
    var maketerName: String,
    var marketerCode: String,
    var marketerId: String,
    var orderNo: String,
    var orderTypeName: String,
    var purchasePartyId: String,
    var purchasePartyName: String,
    var pvtMarka: String,
    var remark: String,
    var salePartyId: String,
    var salePartyName: String,
    var schemeId: String?,
    var schemeName: String,
    var subPartyId: String?,
    var subPartyName: String,
    var subPartyasRemark: String,
    var totalAmt: Int,
    var totalQty: Int,
    var transportId: String,
    var transportName: String,
    var voucherCodeId: String,
    var voucherCodeNo: String
)