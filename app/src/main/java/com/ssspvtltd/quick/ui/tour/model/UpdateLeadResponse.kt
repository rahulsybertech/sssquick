package com.ssspvtltd.quick.ui.tour.model


data class UpdateLeadResponse(
    val isSuccess: Boolean,
    val applicationMessage: String,
    val errorMessage: String?,
    val fileName: String?,
    val exception: String?,
    val logs: String?,
    val objectId: String?,
    val companyID: String?,
    val returnObject: Any?,
    val statusCode: String?,
    val responseCode: Int
)
