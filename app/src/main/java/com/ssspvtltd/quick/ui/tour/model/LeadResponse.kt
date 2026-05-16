package com.ssspvtltd.quick.ui.tour.model

import com.google.gson.annotations.SerializedName
import com.ssspvtltd.quick.base.recycler.data.BaseViewType
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.CommonViewType

data class LeadResponse(

    @SerializedName("data")
    val data: List<LeadItem> = emptyList(),

    @SerializedName("message")
    val message: String = "",

    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("error")
    val error: Boolean = false,

    @SerializedName("responsecode")
    val responsecode: String = ""
)
data class LeadItem(

    @SerializedName("id")
    val id: String = "",

    @SerializedName("firmName")
    val firmName: String = "",

    @SerializedName("categoryName")
    val categoryName: String = "",

    @SerializedName("gradeName")
    val gradeName: String = "",

    @SerializedName("stateName")
    val stateName: String = "",

    @SerializedName("stationName")
    val stationName: String = "",

    @SerializedName("mobileNo")
    val mobileNo: String = ""
): BaseWidget {
    override val viewType: BaseViewType = CommonViewType.HEADER
    override fun getUniqueId() : String = id
}