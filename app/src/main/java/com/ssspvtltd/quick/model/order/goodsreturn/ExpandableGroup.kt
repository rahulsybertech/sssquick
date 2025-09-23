package com.ssspvtltd.quick.model.order.goodsreturn

data class ExpandableGroup(
    val date: String,
    val items: List<GoodsReturnItem>,
    var isExpanded: Boolean = false
)
