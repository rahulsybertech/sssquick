package com.ssspvtltd.quick_app.model.order.stockinoffice

data class StockInOfficeData(
    val orderDate: String,
    val orderItemList: List<StockInOfficeOrderItem>
)