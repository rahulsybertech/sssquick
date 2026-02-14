package com.ssspvtltd.quick.model.order.add

import com.google.gson.annotations.SerializedName

data class SchemeData(

    @SerializedName("schemeId")
    val schemeId: String?,

    @SerializedName("schemeName")
    val schemeName: String?,

    @SerializedName("aliasName")
    val aliasName: String?,

    @SerializedName("activeStatus")
    val activeStatus: Boolean = false
)
