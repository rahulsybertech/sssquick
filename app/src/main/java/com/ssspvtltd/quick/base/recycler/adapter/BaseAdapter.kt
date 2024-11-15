package com.ssspvtltd.quick.base.recycler.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ssspvtltd.quick.base.recycler.data.BaseDiffUtil
import com.ssspvtltd.quick.base.recycler.data.BaseItem

/**
 * Created by Abhishek Singh on April 29,2023.
 */
abstract class BaseAdapter<T : BaseItem, V : BaseViewHolder> :
    ListAdapter<T, BaseViewHolder>(BaseDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return onCreateRecyclerViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        try {
            onBindRecyclerViewHolder(holder as V, position)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    abstract fun onCreateRecyclerViewHolder(parent: ViewGroup, viewType: Int): V
    abstract fun onBindRecyclerViewHolder(holder: V, position: Int)


    fun getItemOrNull(position: Int): T? {
        return currentList.getOrNull(position)
    }
}