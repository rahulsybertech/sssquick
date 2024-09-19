package com.ssspvtltd.quick.model.checkincheckout

import com.ssspvtltd.quick.base.recycler.data.BaseViewType
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.model.CheckInViewType

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