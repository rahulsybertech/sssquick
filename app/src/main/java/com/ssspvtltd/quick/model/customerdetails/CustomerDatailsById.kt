package com.ssspvtltd.quick.model.customerdetails

data class CustomerDatailsById(
    val id: String,
    val nickNameID: String,
    val accountID: String,
    val mobileNo: String,
    val marketerID: String,
    val nickName: String,
    val accountName: String,
    val date: String,
    val persons: List<Person>
)

data class Person(
    val id: String,
    val personName: String,
    val aadharFrontBase64: String?,
    val aadharBackBase64: String?,
    val frontURL: String?,
    val backURL: String?
)