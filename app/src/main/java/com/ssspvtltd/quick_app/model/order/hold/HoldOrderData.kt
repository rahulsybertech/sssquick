package com.ssspvtltd.quick_app.model.order.hold

data class HoldOrderData(
    val orderDate: String?,
    val orderItemList: List<HoldOrderItem>?
)