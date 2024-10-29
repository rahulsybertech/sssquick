package com.ssspvtltd.quick_app.model.order.goodsreturn

data class GoodsReturnData(
    val billDate: String,
    val goodsReturnDetailList: List<GoodsReturnItem>
)