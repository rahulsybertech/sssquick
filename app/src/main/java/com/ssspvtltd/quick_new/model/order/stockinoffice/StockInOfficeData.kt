package com.ssspvtltd.quick_new.model.order.stockinoffice

data class StockInOfficeData(
    val orderDate: String,
    val orderItemList: List<StockInOfficeOrderItem>
)