package com.ssspvtltd.quick_new.model.order.editorder

data class ItemDetails(
    val amount: Int,
    val itemDetail: List<ItemDetail>,
    val packName: String,
    val pcsId: String,
    val pcsType: Any,
    val pcsTypeName: Any,
    val qty: Int
)