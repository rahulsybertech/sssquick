package com.ssspvtltd.quick.ui.followupcall

import com.ssspvtltd.quick.base.recycler.data.BaseViewType
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.CommonViewType

data class LeadFollowupResponse(
    val status: Boolean,
    val message: String,
    val data: ArrayList<LeadData>
)

data class LeadData(
    val id: String,
    val leadNo: String,
    val accountName: String,
    val subParty: String?,
    val mobileNo: String,
    val date: String,
    val netAmt: String?,
    val lastFollowupDate: String,
    val partyType: String,
    val clubType: String,
    var isFollowupDone: Boolean = false,
    var remark: String
) : BaseWidget {

    override val viewType: BaseViewType = CommonViewType.HEADER

    override fun getUniqueId(): String = id
}
