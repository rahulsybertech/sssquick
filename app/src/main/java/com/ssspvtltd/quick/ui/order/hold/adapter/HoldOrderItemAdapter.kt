package com.ssspvtltd.quick.ui.order.hold.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import com.ssspvtltd.quick.base.recycler.adapter.BaseAdapter
import com.ssspvtltd.quick.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick.databinding.ItemHoldOrderListBinding
import com.ssspvtltd.quick.model.order.hold.HoldOrderItem


class HoldOrderItemAdapter(
    private val orderNo: String?,
    private val orderDate: String?,
) : BaseAdapter<HoldOrderItem.ItemName, HoldOrderItemViewHolder>() {

    override fun onCreateRecyclerViewHolder(
        parent: ViewGroup, viewType: Int
    ): HoldOrderItemViewHolder {
        val binding = ItemHoldOrderListBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        // binding.tvOrderDate.text = orderDate ?: "N/A" // if common
        return HoldOrderItemViewHolder(binding)
    }

    override fun onBindRecyclerViewHolder(holder: HoldOrderItemViewHolder, position: Int) {
        getItemOrNull(position)?.let(holder::bind)
        holder.binding.divider.isInvisible = currentList.lastIndex == position
    }
}

class HoldOrderItemViewHolder(val binding: ItemHoldOrderListBinding) :
    BaseViewHolder(binding) {
    fun bind(item: HoldOrderItem.ItemName) = with(binding) {
        tvItemName.text = item.itemName
        tvAmount.text = item.amount.toString()
        tvQuantity.text = item.qty.toString()
    }
}
