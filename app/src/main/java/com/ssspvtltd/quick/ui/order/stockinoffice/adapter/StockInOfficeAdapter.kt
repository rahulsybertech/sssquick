package com.ssspvtltd.quick.ui.order.stockinoffice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick.base.recycler.adapter.MultiViewAdapter
import com.ssspvtltd.quick.base.recycler.data.CommonViewType
import com.ssspvtltd.quick.base.recycler.data.TitleSubtitleWrapper
import com.ssspvtltd.quick.databinding.ItemStockInOfficeBinding
import com.ssspvtltd.quick.databinding.ItemStockInOfficeHeaderBinding
import com.ssspvtltd.quick.model.order.stockinoffice.StockInOfficeOrderItem
import com.ssspvtltd.quick.utils.CommaSparateAmount
import com.ssspvtltd.quick.utils.DateTimeFormat
import com.ssspvtltd.quick.utils.DateTimeUtils

class StockInOfficeAdapter : MultiViewAdapter() {
    internal var onItemClick: ((StockInOfficeOrderItem) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            CommonViewType.HEADER.id -> {
                val binding = ItemStockInOfficeHeaderBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return StockInOfficeHeaderViewHolder(binding)
            }

            CommonViewType.DATA.id -> {
                val binding = ItemStockInOfficeBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return StockInOfficeViewHolder(binding).apply {
                    itemView.setOnClickListener {
                        getItemOrNull<StockInOfficeOrderItem>(bindingAdapterPosition)?.let {
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
            is StockInOfficeHeaderViewHolder -> {
                getItemOrNull<TitleSubtitleWrapper>(position)?.let(holder::bind)
            }

            is StockInOfficeViewHolder -> {
                getItemOrNull<StockInOfficeOrderItem>(position)?.let(holder::bind)
            }

            else -> super.onBindViewHolder(holder, position)
        }
    }
}

class StockInOfficeHeaderViewHolder(private val binding: ItemStockInOfficeHeaderBinding) :
    BaseViewHolder(binding) {
    fun bind(item: TitleSubtitleWrapper) = with(binding) {
        tvOrderDate.text = DateTimeUtils.format(item.title, "dd-MM-yyyy")
    }
}

class StockInOfficeViewHolder(private val binding: ItemStockInOfficeBinding) :
    BaseViewHolder(binding) {
    fun bind(item: StockInOfficeOrderItem) = with(binding) {
        tvSaleParty.text = getString(R.string.sale_party_format, item.salePartyName)
        tvSupplier.text = getString(R.string.supplier_format, item.supplierName)
        tvSubparty.text = getString(R.string.subparty_format, item.subPartyName) ?: "Self"
        tvOrderAmount.text = getString(R.string.amount_format, CommaSparateAmount.formatIndianAmount(item.amount.toString()))
    }
}
