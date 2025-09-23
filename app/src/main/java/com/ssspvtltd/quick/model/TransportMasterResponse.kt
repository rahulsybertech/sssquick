package com.ssspvtltd.quick.model

data class TransportMasterResponse(
    val data: List<TransportMasterData>?,
    val message: String?,
    val success: Boolean,
    val error: Boolean,
    val responsecode: String?
)

data class TransportMasterData(
    val id: String?,
    val transportName: String?,
    val isTransport: Boolean?
)
