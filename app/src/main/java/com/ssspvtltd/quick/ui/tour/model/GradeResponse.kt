package com.ssspvtltd.quick.ui.tour.model

data class GradeResponse(
    val data: List<GradeItem>,
    val message: String,
    val success: Boolean,
    val error: Boolean,
    val responsecode: String
)
data class GradeItem(
    val id: String,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}
