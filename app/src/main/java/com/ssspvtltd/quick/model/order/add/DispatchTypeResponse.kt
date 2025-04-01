package com.ssspvtltd.quick.model.order.add

import com.google.gson.annotations.SerializedName

data class DispatchTypeResponse(
    @SerializedName("data") val data: List<DispatchTypeList>,
    @SerializedName("message") val message: String,
    @SerializedName("success") val success: Boolean,
    @SerializedName("error") val error: Boolean,
    @SerializedName("responsecode") val responseCode: String
)

data class DispatchTypeList(
    @SerializedName("id") val id: String,
    @SerializedName("value") val value: String
)
