package com.ssspvtltd.quick_new.model.order.add.salepartydetails

data class Data(
    val avgDays: Int,
    var avlLimit: String,
    val defSubPartyId: Any?,
    val defSubPartyName: String,
    val defTransport: List<DefTransport>?,
    val emailId: String,
    val mobileNo: String,
    val subPartyList: List<SubParty>?
)