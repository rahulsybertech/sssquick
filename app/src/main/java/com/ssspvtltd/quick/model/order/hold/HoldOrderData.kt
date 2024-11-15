package com.ssspvtltd.quick.model.order.hold

data class HoldOrderData(
    val orderDate: String?,
    val orderItemList: List<HoldOrderItem>?
)