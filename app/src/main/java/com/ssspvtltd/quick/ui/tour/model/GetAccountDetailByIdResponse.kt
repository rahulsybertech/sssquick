package com.ssspvtltd.quick.ui.tour.model

data class GetAccountDetailByIdResponse(
    val data: Data?,
    val message: String?,
    val success: Boolean?,
    val error: Boolean?,
    val responsecode: String?
)

data class Data(
    val id: String?,
    val firmName: String?,
    val ownerName: String?,
    val gradeId: String?,
    val gradeName: String?,
    val categoryId: String?,
    val categoryName: String?,
    val stationID: String?,
    val stationName: String?,
    val stateID: String?,
    val stateName: String?,
    val mobileNo: String?,
    val whatsappNo: String?,
    val shopAreaSqft: String?,
    val yearlySale: String?,
    val branchName: String?,
    val shopCategory: String?
)