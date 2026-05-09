package com.ssspvtltd.quick.ui.tour.model

data class StationResponse(
    val data: List<StationItem>,
    val message: String,
    val success: Boolean,
    val error: Boolean,
    val responsecode: String
)
data class StationItem(
    val id: String,
    val name: String
) {

    override fun toString(): String {
        return name
    }
}
