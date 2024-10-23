package com.ssspvtltd.quick_new.model.order.pending

data class PendingOrderData(
    val orderDate: String,
    val orderItemList: List<PendingOrderItem>?
)