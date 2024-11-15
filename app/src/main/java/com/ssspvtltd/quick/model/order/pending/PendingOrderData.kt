package com.ssspvtltd.quick.model.order.pending

data class PendingOrderData(
    val orderDate: String,
    val orderItemList: List<PendingOrderItem>?
)