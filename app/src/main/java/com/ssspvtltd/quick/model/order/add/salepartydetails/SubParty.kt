package com.ssspvtltd.quick.model.order.add.salepartydetails

data class SubParty(
    val stationList: List<Station>,
    val subPartyId: String,
    val subPartyName: String,
    val transportList: List<Transport>
)