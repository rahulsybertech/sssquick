package com.ssspvtltd.quick_app.base.recycler.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Abhishek Singh on April 29,2023.
 */
class GridSpacingItemDecoration(
    val spanCount: Int,
    val columnSpacing: Int,
    val rowSpacing: Int,
    val includeEdge: Boolean = false
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) = with(outRect) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount
        if (includeEdge) {
            left = columnSpacing - column * columnSpacing / spanCount
            right = (column + 1) * columnSpacing / spanCount
            if (position == 0) top = columnSpacing
            bottom = rowSpacing
        } else {
            left = column * columnSpacing / spanCount
            right = columnSpacing - (column + 1) * columnSpacing / spanCount
            if (position >= spanCount) top = rowSpacing
        }
    }
}