package com.ssspvtltd.quick_app.base.recycler.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Abhishek Singh on April 29,2023.
 */
class ListSpacingItemDecoration(
    private val spaceHeight: Int,
    private val spaceFromEdge: Int = 0,
    private val addTopAndBottomBoth: Boolean = false,
    private val addTopMostAndBottomMost: Boolean = true
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) = with(outRect) {
        val position = parent.getChildAdapterPosition(view)
        if (addTopMostAndBottomMost && (addTopAndBottomBoth || position == 0)) top = spaceHeight
        if (position != state.itemCount - 1 || addTopMostAndBottomMost) bottom = spaceHeight
        left = spaceFromEdge
        right = spaceFromEdge
    }
}