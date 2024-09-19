package com.ssspvtltd.quick.ui.order.add.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import com.ssspvtltd.quick.base.recycler.adapter.BaseAdapter
import com.ssspvtltd.quick.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick.databinding.ItemPackListBinding
import com.ssspvtltd.quick.model.order.add.additem.PackTypeItem

class PackDataItemAdapter : BaseAdapter<PackTypeItem, PackDataItemViewHolder>() {
    override fun onCreateRecyclerViewHolder(
        parent: ViewGroup, viewType: Int
    ): PackDataItemViewHolder {
        val binding = ItemPackListBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PackDataItemViewHolder((binding))
    }

    override fun onBindRecyclerViewHolder(holder: PackDataItemViewHolder, position: Int) {
        holder.binding.divider.isInvisible = currentList.lastIndex == position
        holder.bind(getItem(position))
    }
}

class PackDataItemViewHolder(val binding: ItemPackListBinding) : BaseViewHolder(binding) {
    fun bind(item: PackTypeItem) = with(binding) {
        tvItem.text = item.itemName
        tvQty.text = item.itemQuantity
    }
}