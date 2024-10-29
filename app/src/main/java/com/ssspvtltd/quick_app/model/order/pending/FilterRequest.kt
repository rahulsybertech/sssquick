package com.ssspvtltd.quick_app.model.order.pending

import java.io.Serializable


data class FilterRequest(
    var fromDate : String?,
    var toDate : String?,
    var supplierIDs :List<SupplierData>?,
    var buyerIDs : List<BuyerData>?,
    var isSupplier : Boolean?,
):Serializable
