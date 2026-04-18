package com.ssspvtltd.quick.model.editCustomer

import com.ssspvtltd.quick.model.customerdetails.PersonModel

data class EditCustomerDetailsResponse(
    val ResponseCode: Int,
    val ResponseStatus: Boolean,
    val ResponseMessage: String,
    val Data: List<EditCustomerData>
)

data class EditCustomerData(
    val id: String,
    val nickNameID: String,
    val accountID: String,
    val mobileNo: String,
    val remark: String,
    val marketerID: String,
    val nickName: String,
    val accountName: String,
    val date: String,
    val persons: List<PersonModel>
)

data class EditPerson(
    val id: String,
    val personName: String,
    val aadharFrontBase64: String?,   // nullable
    val aadharBackBase64: String?,    // nullable
    val frontURL: String,
    val backURL: String
)