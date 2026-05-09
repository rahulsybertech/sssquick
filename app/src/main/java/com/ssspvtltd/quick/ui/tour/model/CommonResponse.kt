package com.ssspvtltd.quick.ui.tour.model

data class CommonResponse(
    val data: Boolean,
    val message: String,
    val success: Boolean,
    val error: Boolean,
    val responsecode: String
)