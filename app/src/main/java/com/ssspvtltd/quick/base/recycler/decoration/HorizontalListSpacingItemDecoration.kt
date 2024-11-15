package com.ssspvtltd.quick.base.recycler.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Abhishek Singh on April 29,2023.
 */
class HorizontalListSpacingItemDecoration(
    private val horizontalSpaceHeight: Int,
    private val verticalSpaceHeight: Int = 0,
    private val excludeStartAndEnd: Boolean = false,
    private val addLeftAndRightBoth: Boolean = false,
    private val startHorizontalSpaceHeight: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) = with(outRect) {
        val position = parent.getChildAdapterPosition(view)
        if (!excludeStartAndEnd && (addLeftAndRightBoth || position == 0)) {
            if (startHorizontalSpaceHeight > 0 && position == 0) {
                left = startHorizontalSpaceHeight
            } else {
                left = horizontalSpaceHeight
            }
        }
        if (verticalSpaceHeight > 0) {
            top = verticalSpaceHeight
            bottom = verticalSpaceHeight
        }
        if (!excludeStartAndEnd || position != state.itemCount - 1) {
            right = horizontalSpaceHeight
        }
    }

}