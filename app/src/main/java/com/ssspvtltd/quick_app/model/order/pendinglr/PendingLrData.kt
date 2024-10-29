package com.ssspvtltd.quick_app.model.order.pendinglr

data class PendingLrData(
    val billDate: String,
    val saleDetailList: List<PendingLrItem>
)