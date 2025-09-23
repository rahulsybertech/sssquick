package com.ssspvtltd.quick.ui.order.add.adapter

import android.view.LayoutInflater
import android.view.View
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
                val item = getItemOrNull<PackType>(position)
                if (item!!.amount.isNullOrEmpty()){
                    expandedPosition=0;
                }else{

                }
                holder.binding.groupActionButton.isVisible  = position == expandedPosition
                holder.binding.clSubTitle.isVisible         = position == expandedPosition


                if (holder.binding.groupActionButton.isVisible){
                      holder.binding.btnDeleteItem.visibility = View.VISIBLE
                    holder.binding.btnEditItem.visibility = View.VISIBLE
                    holder.binding.tvAmount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow, 0)
                    holder.itemView.setBackgroundResource(R.color.grey_100)
                    }else{
                    holder.binding.btnDeleteItem.visibility = View.INVISIBLE
                    holder.binding.btnEditItem.visibility = View.INVISIBLE
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

        if (!packType.amount.isNullOrEmpty()) {
           // tvAmount.visibility = View.VISIBLE

            ct1.visibility=View.VISIBLE
            tvAmountTitle.visibility = View.VISIBLE
            tvQtyTitle.visibility = View.VISIBLE
            tvQuantity.visibility = View.VISIBLE
          //  btnDeleteItem.visibility = View.VISIBLE
          //  btnEditItem.visibility = View.VISIBLE
            tvType.visibility = View.VISIBLE
            tvAmount.text   = packType.amount.toString()
        } else {
          //  tvAmount.visibility = View.GONE
            ct1.visibility=View.GONE
            tvAmountTitle.visibility = View.INVISIBLE
            btnDeleteItem.visibility = View.GONE
            btnEditItem.visibility = View.VISIBLE
            tvQtyTitle.visibility = View.INVISIBLE
            tvQuantity.visibility = View.INVISIBLE
            tvType.visibility = View.INVISIBLE
        }

       //tvAmount.text   = packType.amount.toString()
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