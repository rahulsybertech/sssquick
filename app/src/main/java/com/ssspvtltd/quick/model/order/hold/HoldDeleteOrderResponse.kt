package com.ssspvtltd.quick.model.order.hold


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HoldDeleteOrderResponse(
    @SerializedName("data")
    @Expose
    val `data`: Any?,
    @SerializedName("error")
    @Expose
    val error: Boolean?,
    @SerializedName("message")
    @Expose
    val message: String?,
    @SerializedName("responsecode")
    @Expose
    val responsecode: String?,
    @SerializedName("success")
    @Expose
    val success: Boolean?
)