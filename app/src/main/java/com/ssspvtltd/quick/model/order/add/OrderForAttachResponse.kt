package com.ssspvtltd.quick.model.order.add
data class OrderForAttachResponse(
    val data: List<OrderForAttachData>,
    val message: String,
    val success: Boolean,
    val error: Boolean,
    val responsecode: String
)

data class OrderForAttachData(
    val id: String,
    val orderNo: String,
    val supplierName: String,
    val date: String
)
