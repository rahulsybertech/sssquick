package com.ssspvtltd.quick_new.base.recycler

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Abhishek Singh on April 29,2023.
 */
abstract class RecyclerScrollListener : RecyclerView.OnScrollListener() {

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        when (newState) {
            RecyclerView.SCROLL_STATE_IDLE -> onScroll(false)
            RecyclerView.SCROLL_STATE_DRAGGING -> onScroll(true)
            RecyclerView.SCROLL_STATE_SETTLING -> onScroll(true)
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy <= 0) {
            return
        }
        when (val layoutManager = recyclerView.layoutManager) {
            is GridLayoutManager -> {
                if (!isLoading() && !isLastPage()) {
                    val visibleItemCount = layoutManager.findLastCompletelyVisibleItemPosition() + 1
                    if (visibleItemCount >= layoutManager.itemCount) {
                        loadMoreItems()
                    }
                }
            }

            is LinearLayoutManager -> {
                val visibleItemCount: Int = layoutManager.childCount
                val totalItemCount: Int = layoutManager.itemCount
                val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()
                if (!isLoading() && !isLastPage()) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                        loadMoreItems()
                    }
                }
            }
        }
    }

    abstract fun isLoading(): Boolean
    abstract fun isLastPage(): Boolean
    abstract fun loadMoreItems(): Unit?
    protected open fun onScroll(isScrolling: Boolean) {}
}