package com.ssspvtltd.quick.model.order.pending

import com.ssspvtltd.quick.base.recycler.data.BaseViewType
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.CommonViewType

data class PendingOrderData(
    val orderDate: String,
    val orderItemList: List<PendingOrderItem>?
) : BaseWidget {
    override val viewType: BaseViewType = CommonViewType.HEADER
    override fun getUniqueId() : String = orderDate
}