package com.ssspvtltd.quick.model.customerdetails

data class CustomerList(
    val id: String,
    val nickName: String,
    val accountName: String,
    val marketerMame: String?,
    val date: String,
    val fairName: String?,
    val persons: List<PersonData>
)

data class PersonData(
    val name: String,
    val frontImageURL: String?,
    val backImageURL: String?
)