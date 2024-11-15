package com.ssspvtltd.quick.base.recycler.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.CommonViewType
import com.ssspvtltd.quick.databinding.ItemProgressbarBottomBinding
import com.ssspvtltd.quick.databinding.ItemProgressbarMainBinding

/**
 * Created by Abhishek Singh on April 29,2023.
 */
abstract class MultiViewAdapter : ListAdapter<BaseWidget, BaseViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            CommonViewType.PROGRESS_BAR.id -> {
                val binding = ItemProgressbarMainBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return BaseViewHolder(binding)
            }

            CommonViewType.BOTTOM_PROGRESS_BAR.id -> {
                val binding = ItemProgressbarBottomBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return BaseViewHolder(binding)
            }

            else -> {
                return BaseViewHolder(View(parent.context))
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {}


    override fun getItemViewType(position: Int) =
        currentList[position].viewType?.getID() ?: Int.MIN_VALUE

    inline fun <reified T : BaseWidget> getItemOrNull(position: Int): T? {
        return currentList.getOrNull(position) as? T
    }

    inline fun <reified T : BaseWidget> BaseViewHolder.getItemOrNull(): T? {
        return currentList.getOrNull(bindingAdapterPosition) as? T
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<BaseWidget>() {
            override fun areItemsTheSame(oldItem: BaseWidget, newItem: BaseWidget): Boolean {
                if (oldItem.viewType != newItem.viewType) {
                    Log.i("TaG","Recyclerview UI created ")
                }
                return oldItem.getUniqueId() == newItem.getUniqueId()
            }

            override fun areContentsTheSame(oldItem: BaseWidget, newItem: BaseWidget): Boolean {
                if (oldItem != newItem) {
                    Log.i("TaG","Recyclerview UI created 222")
                }
                return oldItem == newItem
            }
        }
    }
}