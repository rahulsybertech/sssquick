package com.ssspvtltd.quick.ui.order.add.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick.base.recycler.adapter.MultiViewAdapter
import com.ssspvtltd.quick.base.recycler.data.CommonViewType
import com.ssspvtltd.quick.databinding.ItemPackTypeHeaderBinding
import com.ssspvtltd.quick.model.order.add.additem.PackType

class PackDataAdapter : MultiViewAdapter() {
    private var expandedPosition: Int = -1
    var onEditClick: ((PackType) -> Unit)? = null
    var onDeleteClick: ((PackType) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            CommonViewType.DATA.id -> {
                val binding = ItemPackTypeHeaderBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                PackDataViewHolder(binding).apply {
                    itemView.setOnClickListener { setExpanded(bindingAdapterPosition) }
                    this.binding.btnEditItem.setOnClickListener {
                        getItemOrNull<PackType>(bindingAdapterPosition)
                            ?.let { onEditClick?.invoke(it) }
                    }
                    this.binding.btnDeleteItem.setOnClickListener {
                        getItemOrNull<PackType>(bindingAdapterPosition)
                            ?.let { onDeleteClick?.invoke(it) }
                    }
                }
            }

            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is PackDataViewHolder -> {
                holder.binding.groupActionButton.isVisible  = position == expandedPosition
                holder.binding.clSubTitle.isVisible         = position == expandedPosition

                if (holder.binding.groupActionButton.isVisible){
                    holder.binding.tvAmount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow, 0)
                    holder.itemView.setBackgroundResource(R.color.grey_100)
                    }else{
                    holder.binding.tvAmount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_drop_arrow, 0)
                    holder.itemView.setBackgroundResource(R.color.white)
                }
                getItemOrNull<PackType>(position)?.let(holder::bind)
            }

            else -> super.onBindViewHolder(holder, position)
        }
    }

    private fun setExpanded(position: Int) {
        if (position < 0) return
        if (expandedPosition == position) {
            expandedPosition = -1
            notifyItemChanged(position)
        } else {
            val oldExpandedPosition = expandedPosition
            expandedPosition = position
            notifyItemChanged(oldExpandedPosition)
            notifyItemChanged(expandedPosition)
        }
    }
}

class PackDataViewHolder(val binding: ItemPackTypeHeaderBinding) : BaseViewHolder(binding) {
    fun bind(packType: PackType) = with(binding) {
        tvType.text     = packType.packName
        tvQuantity.text = packType.qty
        tvAmount.text   = packType.amount.toString()

        tvQtyTitle.text         = "Qty:"
        tvAmountTitle.text      = "Amt:"
        tvItem.text             = "Items"
        tvQty.text              = "Qty"

        if (recyclerView.adapter == null) {
            recyclerView.adapter = PackDataItemAdapter().apply {
                submitList(packType.itemDetail.orEmpty())
            }
        } else {
            (recyclerView.adapter as? PackDataItemAdapter)?.submitList(packType.itemDetail.orEmpty())
        }
    }
}