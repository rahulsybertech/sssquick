package com.ssspvtltd.quick_new.model.order.goodsreturn

data class GoodsReturnData(
    val billDate: String,
    val goodsReturnDetailList: List<GoodsReturnItem>
)