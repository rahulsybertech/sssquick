package com.ssspvtltd.quick_new.model.order.add.salepartydetails

data class DefTransport(
    val transportId: String,
    val transportName: String,
    val defStation: List<DefStation>?,
)