package com.ssspvtltd.quick_new.base.recycler.data

import androidx.recyclerview.widget.DiffUtil

class BaseDiffUtil<T : BaseItem> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.getUniqueId() == newItem.getUniqueId()
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        // AppLog.print("Test ${oldItem  == newItem }")
        return oldItem == newItem
    }
}