package com.ssspvtltd.quick.ui.tour.model


data class StateResponse(
    val data: List<StateItem>,
    val message: String,
    val success: Boolean,
    val error: Boolean,
    val responsecode: String
)
data class StateItem(
    val id: String,
    val name: String
) {

    override fun toString(): String {
        return name
    }
}