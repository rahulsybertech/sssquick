package com.ssspvtltd.quick.ui.customerDetails.model

import com.ssspvtltd.quick.base.recycler.data.BaseViewType
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.CommonViewType

data class CreateResponse(
    val ResponseCode: Int,
    val ResponseStatus: Boolean,
    val ResponseMessage: String?,
    val Data: List<CreateData>
)

data class CreateData(
    val accountName: String,
    val personName: String,
    val marketerName: String,
    val date: String,
    val personCount: Int
)
    : BaseWidget {
    override val viewType: BaseViewType = CommonViewType.HEADER
    override fun getUniqueId() : String = accountName
}