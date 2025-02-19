package com.ssspvtltd.quick.model.order.stockinoffice

data class StockInOfficeData(
    val orderDate: String,
    val orderItemList: List<StockInOfficeOrderItem>
)


