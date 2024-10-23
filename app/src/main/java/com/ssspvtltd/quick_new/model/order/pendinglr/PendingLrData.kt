package com.ssspvtltd.quick_new.model.order.pendinglr

data class PendingLrData(
    val billDate: String,
    val saleDetailList: List<PendingLrItem>
)