package com.ssspvtltd.quick.model.order.pendinglr

data class PendingLrData(
    val billDate: String,
    val saleDetailList: List<PendingLrItem>
)