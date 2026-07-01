package com.ssspvtltd.quick.ui.tour.model

data class LeadResourceResponse(
    val data: List<LeadResource>,
    val message: String,
    val success: Boolean,
    val error: Boolean,
    val responsecode: String
)

data class LeadResource(
    val id: String,
    val leadTypeName: String
)