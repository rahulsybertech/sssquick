package com.ssspvtltd.quick.ui.tour.model



data class ShopCategoryResponse(
    val data: List<ShopCategoryItem>,
    val message: String,
    val success: Boolean,
    val error: Boolean,
    val responsecode: String
)
data class ShopCategoryItem(
    val id: String,
    val name: String,
    var isSelected: Boolean = false
) {

    override fun toString(): String {
        return name
    }
}
