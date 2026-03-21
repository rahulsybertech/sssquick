package com.ssspvtltd.quick.ui.customerDetails.adapter


import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick.base.recycler.adapter.MultiViewAdapter
import com.ssspvtltd.quick.databinding.AllAccountAdapterBinding
import com.ssspvtltd.quick.model.customerdetails.CustomerList
import com.ssspvtltd.quick.ui.customerDetails.allAccount.PersonAdapter

class CustomerListAdapter : MultiViewAdapter() {
    internal var onItemEditClick: ((CustomerList) -> Unit)? = null
    internal var onItemDeleteClick: ((CustomerList) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                val binding = AllAccountAdapterBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                    return PendingLrViewHolder(
                    binding,
                    onItemEditClick,
                    onItemDeleteClick
                )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is PendingLrViewHolder -> {
                getItemOrNull<CustomerList>(position)?.let {
                    holder.bind(it, position)
                }
            }

            else -> super.onBindViewHolder(holder, position)
        }
    }
}
class PendingLrViewHolder(
    private val binding: AllAccountAdapterBinding,
    private val onItemEditClick: ((CustomerList) -> Unit)?,
    private val onItemDeleteClick: ((CustomerList) -> Unit)?
) : BaseViewHolder(binding) {

    private val personAdapter = PersonAdapter()

    init {
        binding.rvPersons.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = personAdapter
            isNestedScrollingEnabled = false   // ✅ VERY IMPORTANT
        }
    }

    fun bind(item: CustomerList,position: Int) = with(binding) {

        // ✅ Set inner list
        personAdapter.submitList(item.persons ?: emptyList())




        tvSNo.text = "S.No. ${position + 1}"
        tvCustomerName.text = Html.fromHtml(
            "<b>Customer Name : </b> ${item.nickName}",
            Html.FROM_HTML_MODE_LEGACY
        )


        // ✅ Edit click
        imgEdit.setOnClickListener {
            onItemEditClick?.invoke(item)
        }

        // ✅ Delete click
        imgDelete.setOnClickListener {
            onItemDeleteClick?.invoke(item)
        }
    }
}
