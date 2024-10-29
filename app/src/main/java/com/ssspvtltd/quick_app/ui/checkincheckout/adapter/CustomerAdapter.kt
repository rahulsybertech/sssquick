package com.ssspvtltd.quick_app.ui.checkincheckout.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.ssspvtltd.quick_app.R
import com.ssspvtltd.quick_app.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick_app.base.recycler.adapter.MultiViewAdapter
import com.ssspvtltd.quick_app.constants.CheckInType
import com.ssspvtltd.quick_app.databinding.ItemCustomerBinding
import com.ssspvtltd.quick_app.model.CheckInViewType
import com.ssspvtltd.quick_app.model.checkincheckout.CustomerData
import com.ssspvtltd.quick_app.utils.extension.orElse


class CustomerAdapter : MultiViewAdapter() {
    internal var onItemClick: ((CustomerData) -> Unit)? = null
    internal var checkInType: CheckInType? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            // CheckInViewType.CHECK_IN_SHIMMER.id -> {
            //    
            // }

            CheckInViewType.CHECK_IN_DATA.id -> {
                val binding =
                    ItemCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return CustomerVH(binding).apply {
                    itemView.setOnClickListener {
                        getItemOrNull<CustomerData>(bindingAdapterPosition)?.let {
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
            is CustomerVH -> {
                getItemOrNull<CustomerData>(position)?.let(holder::bind)
            }
            else -> super.onBindViewHolder(holder, position)
        }
    }

    inner class CustomerVH(private val binding: ItemCustomerBinding) : BaseViewHolder(binding) {
        fun bind(data: CustomerData) = with(binding) {
            tvAccountNo.text = data.accountCode.orElse("N/A")
            if(data.tlock==true){
                tvAccountNo.setBackgroundResource(
                    R.drawable.orange_bg
                )
                tvAccountNo.setTextColor(getColor(R.color.white))
            }
            else if (data.chkStatus == true ) {
                tvAccountNo.setBackgroundResource(
                    if (data.checkInType == checkInType?.value) R.drawable.bg_green
                    else R.drawable.yellow_bg
                )
                tvAccountNo.setTextColor(getColor(R.color.white))
            } else {
                tvAccountNo.setTextColor(getColor(R.color.black))
                tvAccountNo.setBackgroundResource(R.drawable.edit_bg)
            }
        }
    }
}
