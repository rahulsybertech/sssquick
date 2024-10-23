package com.ssspvtltd.quick_new.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DashBoardDataResponse(
    @SerializedName("data")
    @Expose
    val `data`: Data?,
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
) {

    data class Data(
        @SerializedName("todayOrderCount")
        @Expose
        val todayOrderCount: Any?,
        @SerializedName("totalGRCount")
        @Expose
        val totalGRCount: Any?,
        @SerializedName("totalHoldOrderCount")
        @Expose
        val totalHoldOrderCount: Any?,
        @SerializedName("totalPendingOrderCount")
        @Expose
        val totalPendingOrderCount: Any?,
        @SerializedName("totalSaleCount")
        @Expose
        val totalSaleCount: Int?
    )
}