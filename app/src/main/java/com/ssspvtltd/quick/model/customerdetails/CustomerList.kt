package com.ssspvtltd.quick.model.customerdetails

import com.ssspvtltd.quick.base.recycler.data.BaseViewType
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.CommonViewType

data class CustomerList(
    val id: String,
    val editAllowed: Boolean,
    val nickName: String,
    val accountName: String,
    val marketerMame: String?,
    val date: String,
    val fairName: String?,
    val persons: List<PersonData>
) : BaseWidget {
    override val viewType: BaseViewType = CommonViewType.HEADER
    override fun getUniqueId() : String = id
}

data class PersonData(
    val activeStatus: Boolean,
    val name: String,
    val frontImageURL: String?,
    val backImageURL: String?
)