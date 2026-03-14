package com.ssspvtltd.quick.model.customer

data class AccountNameResponse(
    val ResponseCode: Int,
    val ResponseStatus: Boolean,
    val ResponseMessage: String,
    val BookingTime: Int,
    val AccountNameList: List<AccountName>
)

data class AccountName(
    val id: String,
    val name: String,
    val partyType: String?,   // nullable because JSON has null
    val nickNameID: String,
    val nickName: String
)