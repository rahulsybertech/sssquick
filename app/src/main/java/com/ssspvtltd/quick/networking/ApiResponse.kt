package com.ssspvtltd.quick.networking

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val data: T? = null,
    val error: Boolean? = null,
    val message: String? = null,
    @SerializedName("responsecode")
    val responseCode: String? = null,
    val success: Boolean? = false,
    val remark:String?="",
    val checkinStatus: Boolean? = null,
    //UserDefined
    var apiRequestCode: Int? = null,
    var islimitexceed: Boolean? = null,
)