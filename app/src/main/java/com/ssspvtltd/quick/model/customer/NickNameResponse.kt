package com.ssspvtltd.quick.model.customer

data class NickNameResponse(
    val ResponseCode: Int,
    val ResponseStatus: Boolean,
    val ResponseMessage: String,
    val BookingTime: Int,
    val NickNameList: List<NickName>
)

data class NickName(
    val id: String,
    val name: String,
    val partyType: String?,
    val nickNameID: String?,
    val nickName: String?
)