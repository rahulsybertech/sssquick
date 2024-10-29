package com.ssspvtltd.quick_app.ui.order.hold.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.ssspvtltd.quick_app.R
import com.ssspvtltd.quick_app.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick_app.base.recycler.adapter.MultiViewAdapter
import com.ssspvtltd.quick_app.base.recycler.data.CommonViewType
import com.ssspvtltd.quick_app.base.recycler.data.TitleSubtitleWrapper
import com.ssspvtltd.quick_app.databinding.ItemHoldOrderBinding
import com.ssspvtltd.quick_app.databinding.ItemHoldOrderHeaderBinding
import com.ssspvtltd.quick_app.model.order.hold.HoldOrderItem
import com.ssspvtltd.quick_app.utils.DateTimeFormat
import com.ssspvtltd.quick_app.utils.DateTimeUtils


class HoldOrderAdapter : MultiViewAdapter() {
    internal var onItemClick: ((HoldOrderItem) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            CommonViewType.HEADER.id -> {
                val binding = ItemHoldOrderHeaderBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return HoldOrderHeaderViewHolder(binding)
            }

            CommonViewType.DATA.id -> {
                val binding = ItemHoldOrderBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return HoldOrderViewHolder(binding).apply {
                    itemView.setOnClickListener {
                        getItemOrNull<HoldOrderItem>(bindingAdapterPosition)?.let {
                            onItemClick?.invoke(it)
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
            is HoldOrderHeaderViewHolder -> {
                getItemOrNull<TitleSubtitleWrapper>(position)?.let(holder::bind)
            }

            is HoldOrderViewHolder -> {
                getItemOrNull<HoldOrderItem>(position)?.let(holder::bind)
            }

            else -> super.onBindViewHolder(holder, position)
        }
    }
}

class HoldOrderHeaderViewHolder(private val binding: ItemHoldOrderHeaderBinding) :
    BaseViewHolder(binding) {
    fun bind(item: TitleSubtitleWrapper) = with(binding) {
        tvOrderDate.text = DateTimeUtils.formatDate(
            item.title, DateTimeFormat.DATE_TIME_FORMAT1,
            DateTimeFormat.DATE_TIME_FORMAT3
        )
    }
}

class HoldOrderViewHolder(private val binding: ItemHoldOrderBinding) :
    BaseViewHolder(binding) {
    fun bind(item: HoldOrderItem) = with(binding) {
        tvSaleBillNo.text = getString(R.string.sale_bill_no_format, item.orderNo)
        tvSaleParty.text = getString(R.string.sale_party_format, item.salePartyName)
        tvSupplier.text = getString(R.string.supplier_format, item.supplierName)
        tvSubParty.text = "Qty: " + (item.qty ?: "").toString()
        tvItemName.text = item.type ?: "--"
        tvOrderStatus.text = item.status ?: "--"
        tvOrderAmount.text = getString(R.string.amount_format,item.amount)
    }
}
