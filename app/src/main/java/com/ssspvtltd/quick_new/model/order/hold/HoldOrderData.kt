package com.ssspvtltd.quick_new.model.order.hold

data class HoldOrderData(
    val orderDate: String?,
    val orderItemList: List<HoldOrderItem>?
)