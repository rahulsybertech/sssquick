package com.ssspvtltd.quick_app.ui.checkincheckout.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.ssspvtltd.quick_app.R
import com.ssspvtltd.quick_app.constants.CheckInType
import com.ssspvtltd.quick_app.databinding.ItemCustomerBinding
import com.ssspvtltd.quick_app.model.checkincheckout.CustomerData
import com.ssspvtltd.quick_app.utils.extension.orElse

class NewCustomerAdapter(private val context: Context) : RecyclerView.Adapter<NewCustomerAdapter.CustomerVH>() {
    private var customerList: List<CustomerData> = listOf()
    internal var onItemClick: ((CustomerData) -> Unit)? = null
    internal var checkInType: CheckInType? = null

    fun setCustomers(customers: List<CustomerData>) {
        customerList = customers
        notifyDataSetChanged() // Notify the adapter about data changes
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerVH {
        val binding = ItemCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerVH(binding).apply {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val customerData = customerList[position]
                    Log.i("TaG","item Click recycler view -------> $customerData")
                    onItemClick?.invoke(customerData)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: CustomerVH, position: Int) {
        holder.bind(customerList[position])
    }

    override fun getItemCount(): Int {
        return customerList.size
    }

    inner class CustomerVH(private val binding: ItemCustomerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CustomerData) = with(binding) {
            tvAccountNo.text = data.accountCode.orElse("N/A")
            if (data.tlock == true) {
                tvAccountNo.setBackgroundResource(R.drawable.orange_bg)
                tvAccountNo.setTextColor(getColor(context,R.color.white))
            } else if (data.chkStatus == true) {
                tvAccountNo.setBackgroundResource(
                    if (data.checkInType == checkInType?.value) R.drawable.bg_green
                    else R.drawable.yellow_bg
                )
                tvAccountNo.setTextColor(getColor(context, R.color.white))
            } else {
                tvAccountNo.setTextColor(getColor(context, R.color.black))
                tvAccountNo.setBackgroundResource(R.drawable.edit_bg)
            }
        }
    }
}
