package com.ssspvtltd.quick.ui.order.pendinglr.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick.base.recycler.adapter.MultiViewAdapter
import com.ssspvtltd.quick.base.recycler.data.CommonViewType
import com.ssspvtltd.quick.base.recycler.data.TitleSubtitleWrapper
import com.ssspvtltd.quick.databinding.ItemPendingLrBinding
import com.ssspvtltd.quick.databinding.ItemPendingLrHeaderBinding
import com.ssspvtltd.quick.model.order.pendinglr.PendingLrItem
import com.ssspvtltd.quick.utils.DateTimeFormat
import com.ssspvtltd.quick.utils.DateTimeUtils

class PendingLrAdapter : MultiViewAdapter() {
    internal var onItemClick: ((PendingLrItem) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            CommonViewType.HEADER.id -> {
                val binding = ItemPendingLrHeaderBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return PendingLrHeaderViewHolder(binding)
            }

            CommonViewType.DATA.id -> {
                val binding = ItemPendingLrBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return PendingLrViewHolder(binding).apply {
                    itemView.setOnClickListener {
                        getItemOrNull<PendingLrItem>(bindingAdapterPosition)?.let {
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
            is PendingLrHeaderViewHolder -> {
                getItemOrNull<TitleSubtitleWrapper>(position)?.let(holder::bind)
            }

            is PendingLrViewHolder -> {
                getItemOrNull<PendingLrItem>(position)?.let(holder::bind)
            }

            else -> super.onBindViewHolder(holder, position)
        }
    }
}

class PendingLrHeaderViewHolder(private val binding: ItemPendingLrHeaderBinding) :
    BaseViewHolder(binding) {
    fun bind(item: TitleSubtitleWrapper) = with(binding) {
        tvOrderDate.text = DateTimeUtils.formatDate(
            item.title, DateTimeFormat.DATE_TIME_FORMAT1,
            DateTimeFormat.DATE_TIME_FORMAT3
        )
    }
}

class PendingLrViewHolder(private val binding: ItemPendingLrBinding) :
    BaseViewHolder(binding) {
    fun bind(item: PendingLrItem) = with(binding) {
        tvSaleBillNo.text = getString(R.string.sale_bill_no_format, item.billNo)
        tvSaleParty.text = getString(R.string.sale_party_format, item.salePartyName)
        tvSupplier.text = getString(R.string.supplier_format, item.supplierName)
        // tvSubParty.text = item.subPartyName ?: "Self"
        tvSubParty.text = "Qty: ${(item.qty ?: " - ")}"
        tvItemName.text = "Jeans"
        tvOrderStatus.text = "Pending"
        tvOrderAmount.text = getString(R.string.amount_format, item.amount)
    }
}
