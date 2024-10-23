package com.ssspvtltd.quick_new.base.recycler.decoration

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Abhishek Singh on April 29,2023.
 */
class HorizontalCustomRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {
    private val TAG = "HorizontalRecyclerView"
    private var touchListener: OnItemTouchListener? = null
    private var preX = 0f
    private var preY = 0f

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.i(TAG, "onAttachedToWindow")
        setOnItemTouchListener()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (touchListener != null) {
            Log.i(TAG, "onDetachedFromWindow")
            removeOnItemTouchListener(touchListener!!)
        }
        touchListener = null
    }

    private fun setOnItemTouchListener() {
        touchListener = object : OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        rv.parent.requestDisallowInterceptTouchEvent(true)
                    }

                    MotionEvent.ACTION_MOVE -> {
                        if (Math.abs(e.x - preX) > Math.abs(e.y - preY)) {
                            val posScroll = rv.canScrollHorizontally(1)
                            val negScroll = rv.canScrollHorizontally(-1)
                            Log.i(TAG, "posScroll $posScroll ${e.x}, $preX negScroll $negScroll")
                            if (posScroll && e.x - preX < 0) {
                                rv.parent.requestDisallowInterceptTouchEvent(true)
                            } else if (negScroll && e.x - preX > 0) {
                                rv.parent.requestDisallowInterceptTouchEvent(true)
                            } else {
                                rv.parent.requestDisallowInterceptTouchEvent(false)
                            }
                        } else {
                            rv.parent.requestDisallowInterceptTouchEvent(false)
                        }
                    }

                    else -> {
                        /*rv.parent
                            .requestDisallowInterceptTouchEvent(false)*/
                    }
                }

                preX = e.x
                preY = e.y
                return false
                /*val action = e.action
                if (rv.canScrollHorizontally(1) || rv.canScrollHorizontally(-1)) {
                    when (action) {
                        MotionEvent.ACTION_MOVE ->
                            rv.parent.requestDisallowInterceptTouchEvent(true)
                    }
                    return false
                } else {
                    when (action) {
                        MotionEvent.ACTION_MOVE ->
                            rv.parent.requestDisallowInterceptTouchEvent(false)
                    }
                    //rv.removeOnItemTouchListener(this)
                    return true
                }*/
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        }

        addOnItemTouchListener(touchListener!!)
    }
}