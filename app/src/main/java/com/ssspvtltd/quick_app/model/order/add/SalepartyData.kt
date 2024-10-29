package com.ssspvtltd.quick_app.model.order.add

import com.ssspvtltd.quick_app.base.recycler.data.BaseViewType
import com.ssspvtltd.quick_app.base.recycler.data.BaseWidget
import com.ssspvtltd.quick_app.model.CheckInViewType

data class SalepartyData(
    val accountID: String?, val accountName: String?, val rno: String?
):BaseWidget {
    override fun getUniqueId(): String = (accountID + rno)
    override val viewType: BaseViewType get() = CheckInViewType.CHECK_IN_DATA
}
