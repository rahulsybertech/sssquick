package com.ssspvtltd.quick_app.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class GetStockInOfficeOrderDetailsRequest(
    @SerializedName("buyerIDs")
    @Expose
    val buyerIDs: List<Any?>?,
    @SerializedName("fromDate")
    @Expose
    val fromDate: Any?,
    @SerializedName("isSupplier")
    @Expose
    val isSupplier: Boolean?,
    @SerializedName("supplierIDs")
    @Expose
    val supplierIDs: List<Any?>?,
    @SerializedName("toDate")
    @Expose
    val toDate: Any?
)