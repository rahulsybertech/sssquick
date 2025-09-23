package com.ssspvtltd.quick.model.gr

import com.ssspvtltd.quick.model.GoodsReturnItem

data class GoodsReturnDataGr(
    val id: String?,
    val grInDate: String?,
    val customerId: String?,
    val customerName: String?,
    val subPartyId: String?,
    val subPartyName: String?,
    val supplierId: String?,
    val supplierName: String?,
    val transportId: String?,
    val transportName: String?,
    val marketerId: String?,
    val marketerName: String?,
    val courierId: String?,
    val courierName: String?,
    val parcelQty: Double?,
    val voucherNo: Int?,
    val courierNo: String?,
    val traceIdentifier: String?,
    val goodsReturnItemList: List<GoodsReturnItem>?,
    val itemDetailsImageList: List<ItemDetailsImage>?
)

data class ItemDetailsImage(
    val id: String?,
    val documentType: String?,
    val imagePath: String?
)
