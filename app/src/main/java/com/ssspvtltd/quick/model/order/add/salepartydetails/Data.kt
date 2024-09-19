package com.ssspvtltd.quick.model.order.add.salepartydetails

data class Data(
    val avgDays: Int,
    val avlLimit: Double,
    val defSubPartyId: Any?,
    val defSubPartyName: String,
    val defTransport: List<DefTransport>?,
    val emailId: String,
    val mobileNo: String,
    val subPartyList: List<SubParty>?
)