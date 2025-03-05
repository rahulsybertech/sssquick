package com.ssspvtltd.quick.model.order.add.salepartyNewList

data class SubParty(
    val subPartyId: String,
    val subPartyName: String,
    val transportList: List<DefTransport>
)
