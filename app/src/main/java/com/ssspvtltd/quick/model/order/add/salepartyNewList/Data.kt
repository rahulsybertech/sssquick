package com.ssspvtltd.quick.model.order.add.salepartyNewList

data class Data(
    val avlLimit: Double,
    val avgDays: Int,
    val emailId: String,
    val mobileNo: String,
    val defSubPartyId: String?,
    val defSubPartyName: String,
    val defTransport: String?,
    val subPartyList: List<SubParty>
)
