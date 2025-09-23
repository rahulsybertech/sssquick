package com.ssspvtltd.quick.ui.order.pending.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick.base.recycler.adapter.MultiViewAdapter
import com.ssspvtltd.quick.base.recycler.data.CommonViewType
import com.ssspvtltd.quick.base.recycler.data.TitleSubtitleWrapper
import com.ssspvtltd.quick.databinding.ItemPendingOrderBinding
import com.ssspvtltd.quick.databinding.ItemPendingOrderByCustomerBinding
import com.ssspvtltd.quick.databinding.ItemPendingOrderHeaderBinding
import com.ssspvtltd.quick.model.order.pending.PendingOrderItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PendingOrderByCustomerAdapter : MultiViewAdapter() {
    internal var onItemClick: ((PendingOrderItem) -> Unit)? = null
    internal var onEditClick: ((PendingOrderItem) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            CommonViewType.HEADER.id -> {
                val binding = ItemPendingOrderHeaderBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return PendingOrderByCustomerHeaderViewHolder(binding)
            }

            CommonViewType.DATA.id -> {
                val binding = ItemPendingOrderByCustomerBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return PendingOrderByCustomerViewHolder(binding).apply {
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
            is PendingOrderByCustomerHeaderViewHolder -> {
                getItemOrNull<TitleSubtitleWrapper>(position)?.let(holder::bind)
            }

            is PendingOrderByCustomerViewHolder -> {
                getItemOrNull<PendingOrderItem>(position)?.let(holder::bind)
            }

            else -> super.onBindViewHolder(holder, position)
        }
    }
}

class PendingOrderByCustomerHeaderViewHolder(private val binding: ItemPendingOrderHeaderBinding) :
    BaseViewHolder(binding) {
    fun bind(item: TitleSubtitleWrapper) = with(binding) {

        println("DATE_CHECKING ${item.id}, ${item.title}, ${item.subtitle}, ${item.actionText}, ${item.anyObject}, ${item.viewType}")

        val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        tvOrderDate.text = LocalDateTime.parse(item.title, DateTimeFormatter.ISO_DATE_TIME).format(outputFormatter)

        // tvOrderDate.text = DateTimeUtils.formatDate(
        //     item.id, DateTimeFormat.DATE_TIME_FORMAT1,
        //     DateTimeFormat.DATE_TIME_FORMAT3
        // )
    }
}

class PendingOrderByCustomerViewHolder(private val binding: ItemPendingOrderByCustomerBinding) :
    BaseViewHolder(binding) {
    fun bind(item: PendingOrderItem) = with(binding) {
        tvSaleBillNo.text = getString(R.string.order_no_format, item.orderNo)
        // prefHelper.setOrderId(getString(R.string.order_no_format, item.orderNo)
        tvSaleParty.text = getString(R.string.sale_party_format, item.salePartyName)
        tvSupplier.text = getString(R.string.supplier_format, item.supplierName)
        tvSubParty.text = """Qty: ${(item.qty ?: "--")}"""
        tvItemName.text = item.type ?: ""

        tvOrderStatus.text = item.status ?: ""
        when (item.status) {
            "PENDING" -> {
              //  tvOrderStatus.setTextColor(ContextCompat.getColor(tvOrderStatus.context, R.color.red_2))
                tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(tvOrderStatus.context, R.color.deep_orange_300)
            }
            "HOLD" -> {
              //  tvOrderStatus.setTextColor(ContextCompat.getColor(tvOrderStatus.context, R.color.green))
                tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(tvOrderStatus.context, R.color.warning_bg)
            }
            else -> {
            //    tvOrderStatus.setTextColor(ContextCompat.getColor(tvOrderStatus.context, R.color.black))
                tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(tvOrderStatus.context, R.color.deep_orange_300)
            }
        }
        tvOrderAmount.text = getString(R.string.amount_format,item.amount)
    }
}