package com.ssspvtltd.quick.ui.order.pending.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import com.ssspvtltd.quick.base.recycler.adapter.BaseAdapter
import com.ssspvtltd.quick.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick.databinding.ItemPendingOrderListBinding
import com.ssspvtltd.quick.model.order.pending.PendingOrderItem


class PendingOrderItemAdapter(
    private val orderNo: String?,
    private val orderDate: String?,
) : BaseAdapter<PendingOrderItem.ItemName, PendingOrderItemViewHolder>() {

    override fun onCreateRecyclerViewHolder(
        parent: ViewGroup, viewType: Int
    ): PendingOrderItemViewHolder {
        val binding = ItemPendingOrderListBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        // binding.tvOrderNo.text = orderNo  // If Common
        return PendingOrderItemViewHolder(binding)
    }

    override fun onBindRecyclerViewHolder(holder: PendingOrderItemViewHolder, position: Int) {
        getItemOrNull(position)?.let(holder::bind)
        holder.binding.divider.isInvisible = currentList.lastIndex == position
    }
}

class PendingOrderItemViewHolder( val binding: ItemPendingOrderListBinding) :
    BaseViewHolder(binding) {
    fun bind(item: PendingOrderItem.ItemName) = with(binding) {
        tvItemName.text = item.itemName
        tvAmount.text = item.amount.toString()
        tvQuantity.text = item.qty.toString()

    }
}
