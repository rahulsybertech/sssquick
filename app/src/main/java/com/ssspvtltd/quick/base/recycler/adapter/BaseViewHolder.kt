package com.ssspvtltd.quick.base.recycler.adapter

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Created by Abhishek Singh on April 29,2023.
 */
open class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    constructor(binding: ViewBinding) : this(binding.root)

    protected fun getString(@StringRes resId: Int, vararg args: Any?): String {
        return itemView.resources.getString(resId, *args)
    }

    @ColorInt
    protected fun getColor(@ColorRes resId: Int): Int {
        return ContextCompat.getColor(itemView.context, resId)
    }

    protected fun getDrawable(@DrawableRes resId: Int): Drawable? {
        return ContextCompat.getDrawable(itemView.context, resId)

    }
}