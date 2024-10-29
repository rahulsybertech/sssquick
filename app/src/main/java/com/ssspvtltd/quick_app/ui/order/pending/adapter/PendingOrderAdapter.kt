package com.ssspvtltd.quick_app.ui.order.pending.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.ssspvtltd.quick_app.R
import com.ssspvtltd.quick_app.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick_app.base.recycler.adapter.MultiViewAdapter
import com.ssspvtltd.quick_app.base.recycler.data.CommonViewType
import com.ssspvtltd.quick_app.base.recycler.data.TitleSubtitleWrapper
import com.ssspvtltd.quick_app.databinding.ItemPendingOrderBinding
import com.ssspvtltd.quick_app.databinding.ItemPendingOrderHeaderBinding
import com.ssspvtltd.quick_app.model.order.pending.PendingOrderItem
import com.ssspvtltd.quick_app.utils.DateTimeFormat
import com.ssspvtltd.quick_app.utils.DateTimeUtils


class PendingOrderAdapter : MultiViewAdapter() {
    internal var onItemClick: ((PendingOrderItem) -> Unit)? = null
    internal var onEditClick: ((PendingOrderItem) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            CommonViewType.HEADER.id -> {
                val binding = ItemPendingOrderHeaderBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return PendingOrderHeaderViewHolder(binding)
            }

            CommonViewType.DATA.id -> {
                val binding = ItemPendingOrderBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return PendingOrderViewHolder(binding).apply {
                    itemView.setOnClickListener {
                        getItemOrNull<PendingOrderItem>(bindingAdapterPosition)?.let {
                            onItemClick?.invoke(it)
                            // onEditClick?.invoke(it)
                        }
                    }
                }
            }

            else -> {
                return super.onCreateViewHolder(parent, viewType)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is PendingOrderHeaderViewHolder -> {
                getItemOrNull<TitleSubtitleWrapper>(position)?.let(holder::bind)
            }

            is PendingOrderViewHolder -> {
                getItemOrNull<PendingOrderItem>(position)?.let(holder::bind)
            }

            else -> super.onBindViewHolder(holder, position)
        }
    }
}

class PendingOrderHeaderViewHolder(private val binding: ItemPendingOrderHeaderBinding) :
    BaseViewHolder(binding) {
    fun bind(item: TitleSubtitleWrapper) = with(binding) {
        tvOrderDate.text = DateTimeUtils.formatDate(
            item.title, DateTimeFormat.DATE_TIME_FORMAT1,
            DateTimeFormat.DATE_TIME_FORMAT3
        )
    }
}

class PendingOrderViewHolder(private val binding: ItemPendingOrderBinding) :
    BaseViewHolder(binding) {
    fun bind(item: PendingOrderItem) = with(binding) {
        tvSaleBillNo.text = getString(R.string.order_no_format, item.orderNo)
        tvSaleParty.text = getString(R.string.sale_party_format, item.salePartyName)
        tvSupplier.text = getString(R.string.supplier_format, item.supplierName)
        tvSubParty.text = """Qty: ${(item.qty ?: "--")}"""
        tvItemName.text = item.type ?: ""
        tvOrderStatus.text = (item.qty ?: "--").toString()
        tvOrderAmount.text = getString(R.string.amount_format,item.amount)
    }
}
