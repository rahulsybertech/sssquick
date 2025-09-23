package com.ssspvtltd.quick.ui.order.goodsreturn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick.base.recycler.adapter.MultiViewAdapter
import com.ssspvtltd.quick.base.recycler.data.CommonViewType
import com.ssspvtltd.quick.base.recycler.data.TitleSubtitleWrapper
import com.ssspvtltd.quick.databinding.ItemGoodsReturnBinding
import com.ssspvtltd.quick.databinding.ItemGoodsReturnHeaderBinding
import com.ssspvtltd.quick.model.order.goodsreturn.GoodsReturnItem
import com.ssspvtltd.quick.utils.DateTimeFormat
import com.ssspvtltd.quick.utils.DateTimeUtils


class GoodsReturnAdapter : MultiViewAdapter() {
    internal var onItemClick: ((GoodsReturnItem) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            CommonViewType.HEADER.id -> {
                val binding = ItemGoodsReturnHeaderBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return GoodsReturnHeaderViewHolder(binding)
            }

            CommonViewType.DATA.id -> {
                val binding = ItemGoodsReturnBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return GoodsReturnViewHolder(binding).apply {
                    itemView.setOnClickListener {
                        getItemOrNull<GoodsReturnItem>(bindingAdapterPosition)?.let {
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
            is GoodsReturnHeaderViewHolder -> {
                getItemOrNull<TitleSubtitleWrapper>(position)?.let(holder::bind)
            }

            is GoodsReturnViewHolder -> {
                getItemOrNull<GoodsReturnItem>(position)?.let(holder::bind)
            }

            else -> super.onBindViewHolder(holder, position)
        }
    }
}

class GoodsReturnHeaderViewHolder(private val binding: ItemGoodsReturnHeaderBinding) :
    BaseViewHolder(binding) {
    fun bind(item: TitleSubtitleWrapper) = with(binding) {
        tvOrderDate.text = DateTimeUtils.formatDate(
            item.title, DateTimeFormat.DATE_TIME_FORMAT1,
            DateTimeFormat.DATE_TIME_FORMAT3
        )
    }
}

class GoodsReturnViewHolder(private val binding: ItemGoodsReturnBinding) :
    BaseViewHolder(binding) {
    fun bind(item: GoodsReturnItem) = with(binding) {
        tvSaleBillNo.text = getString(R.string.sale_bill_no_format, item.saleBillNo)
        tvSaleParty.text = getString(R.string.sale_party_format, item.salePartyName)
        tvSupplier.text = getString(R.string.supplier_format, item.supplierName)
        // tvSubParty.text = item.subPartyName?:"Self"

        tvSubParty.text ="Qty: ${(item.qty ?: " - ")}"
        // tvItemName.text = "Jeans"
        tvOrderStatus.text = item.status?:"Pending"
        tvOrderAmount.text = getString(R.string.amount_format,item.amount)
    }
}
