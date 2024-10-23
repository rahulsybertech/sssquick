package com.ssspvtltd.quick_new.model.checkincheckout

import com.ssspvtltd.quick_new.base.recycler.data.BaseViewType
import com.ssspvtltd.quick_new.base.recycler.data.BaseWidget
import com.ssspvtltd.quick_new.model.CheckInViewType

data class CustomerData(
    val id: String?,
    val accountCode: String?,
    var checkInType: String?,
    var chkStatus: Boolean?,
    val tlock:Boolean?,
    val alertMessage:String?,
) : BaseWidget {
    override fun getUniqueId(): String = (id + accountCode)
    override val viewType: BaseViewType get() = CheckInViewType.CHECK_IN_DATA

}