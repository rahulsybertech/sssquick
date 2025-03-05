package com.ssspvtltd.quick.model.order.add.salepartyNewList

data class DefTransport(
    val transportId: String,
    val transportName: String,
    val defaultStatus: Boolean,
    val stationList: List<AllStation>
)
