package com.ssspvtltd.quick.model

data class SubPartyGRResponse(
    val data: List<SubPartyGRData>?,
    val message: String?,
    val success: Boolean,
    val error: Boolean,
    val responsecode: String?
)

data class SubPartyGRData(
    val accountName: String?,
    val accountId: String?,
    val transportId: String?,
    val transportName: String?,
    val stationID: String?,
    val stationName: String?,
    val tlock: Boolean?,
    val incomingTransaction: Boolean?,
    val outgoingTransaction: Boolean?,
    val lockName: String?,
    val alertMessage: String?,
    val emailID: String?,
    val mobileNo: String?
)
