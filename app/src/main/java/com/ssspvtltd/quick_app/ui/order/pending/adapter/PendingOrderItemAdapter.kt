package com.ssspvtltd.quick_app.ui.order.pending.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import com.ssspvtltd.quick_app.base.recycler.adapter.BaseAdapter
import com.ssspvtltd.quick_app.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick_app.databinding.ItemPendingOrderListBinding
import com.ssspvtltd.quick_app.model.order.pending.PendingOrderItem


class PendingOrderItemAdapter(
    private val orderNo: String?,
    private val orderDate: String?,
) : BaseAdapter<PendingOrderItem.ItemName, PendingOrderItemViewHolder>() {

    override fun onCreateRecyclerViewHolder(
        parent: ViewGroup, viewType: Int
    ): PendingOrderItemViewHolder {
        val binding = ItemPendingOrderListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        Log.i("TaG","recycler view Data --------===>$item")
        tvItemName.text = item.itemName
        tvAmount.text = item.amount.toString()
        tvQuantity.text = item.qty.toString()

    }
}
