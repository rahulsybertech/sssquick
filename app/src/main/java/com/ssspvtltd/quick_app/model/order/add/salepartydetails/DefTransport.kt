package com.ssspvtltd.quick_app.model.order.add.salepartydetails

data class DefTransport(
    val transportId: String,
    val transportName: String,
    val defStation: List<DefStation>?,
)