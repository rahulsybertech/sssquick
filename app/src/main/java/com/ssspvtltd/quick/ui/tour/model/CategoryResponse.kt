package com.ssspvtltd.quick.ui.tour.model


data class CategoryResponse(
    val data: List<CategoryItem>,
    val message: String,
    val success: Boolean,
    val error: Boolean,
    val responsecode: String
)
data class CategoryItem(
    val id: String,
    val name: String,
    var isSelected: Boolean = false
) {

    override fun toString(): String {
        return name
    }
}