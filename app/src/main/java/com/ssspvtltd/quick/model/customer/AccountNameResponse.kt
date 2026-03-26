package com.ssspvtltd.quick.model.customer

import com.ssspvtltd.quick.model.customerdetails.CustomerList

data class AccountNameResponse(
    val ResponseCode: Int,
    val ResponseStatus: Boolean,
    val ResponseMessage: String,
    val BookingTime: Int,
    val AccountNameList: List<AccountName>,
    val Data: List<CustomerList>
)

data class AccountName(
    val id: String,
    val name: String,
    val partyType: String?,   // nullable because JSON has null
    val nickNameID: String,
    val nickName: String
){
    override fun toString(): String {
        return name ?: name   // 👈 display value
    }
}