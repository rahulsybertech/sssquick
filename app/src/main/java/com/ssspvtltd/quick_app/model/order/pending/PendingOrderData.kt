package com.ssspvtltd.quick_app.model.order.pending

data class PendingOrderData(
    val orderDate: String,
    val orderItemList: List<PendingOrderItem>?
)