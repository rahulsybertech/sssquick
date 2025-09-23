package com.ssspvtltd.quick.model

data class IntResponse(
    val data: Int,
    val message: String,
    val success: Boolean,
    val error: Boolean,
    val responsecode: String
)
