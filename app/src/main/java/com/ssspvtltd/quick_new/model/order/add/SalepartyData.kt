package com.ssspvtltd.quick_new.model.order.add

import com.ssspvtltd.quick_new.base.recycler.data.BaseViewType
import com.ssspvtltd.quick_new.base.recycler.data.BaseWidget
import com.ssspvtltd.quick_new.model.CheckInViewType

data class SalepartyData(
    val accountID: String?, val accountName: String?, val rno: String?
):BaseWidget {
    override fun getUniqueId(): String = (accountID + rno)
    override val viewType: BaseViewType get() = CheckInViewType.CHECK_IN_DATA
}
