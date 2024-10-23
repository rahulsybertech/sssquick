package com.ssspvtltd.quick_new.model.order.editorder

data class ItemDetail(
    val amount: Int,
    val colorName: String,
    val id: String,
    val itemId: String,
    val itemName: String,
    val itemQuantity: Int,
    val sizeName: String
)