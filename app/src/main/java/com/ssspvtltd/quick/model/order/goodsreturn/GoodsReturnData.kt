package com.ssspvtltd.quick.model.order.goodsreturn

data class GoodsReturnData(
    val billDate: String,
    val id: String,
    val goodsReturnPrimaryData: List<GoodsReturnItem>,
    val goodsReturnDetailList: List<GoodsReturnItem>,
    var isExpanded: Boolean = false
)