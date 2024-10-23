package com.ssspvtltd.quick_new.model

import com.google.gson.annotations.SerializedName

data class DashBoardDataResponse(
    @SerializedName("data")
    val data: Data?,
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("responsecode")
    val responsecode: String?,
    @SerializedName("success")
    val success: Boolean?
) {

    data class Data(
        @SerializedName("todayOrderCount")
        val todayOrderCount: Int?,
        @SerializedName("totalGRCount")
        val totalGRCount: Int?,
        @SerializedName("totalHoldOrderCount")
        val totalHoldOrderCount: Int?,
        @SerializedName("totalPendingOrderCount")
        val totalPendingOrderCount: Int?,
        @SerializedName("totalSaleCount")
        val totalSaleCount: Int?
    )
}