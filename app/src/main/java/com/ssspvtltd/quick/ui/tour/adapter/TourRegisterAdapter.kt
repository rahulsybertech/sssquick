package com.ssspvtltd.quick.ui.tour.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ssspvtltd.quick.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick.base.recycler.adapter.MultiViewAdapter
import com.ssspvtltd.quick.databinding.AdapterTourRegisterListBinding
import com.ssspvtltd.quick.model.customerdetails.CustomerList
import com.ssspvtltd.quick.ui.customerDetails.allAccount.PersonAdapter

class TourRegisterAdapter    : MultiViewAdapter() {
    internal var onItemEditClick: ((CustomerList) -> Unit)? = null
    internal var onItemDeleteClick: ((CustomerList) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = AdapterTourRegisterListBinding
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
    private val binding: AdapterTourRegisterListBinding,
    private val onItemEditClick: ((CustomerList) -> Unit)?,
    private val onItemDeleteClick: ((CustomerList) -> Unit)?
) : BaseViewHolder(binding) {

    private val personAdapter = PersonAdapter()

    fun bind(item: CustomerList,position: Int) = with(binding) {


        if (item.editAllowed) {

            layoutEdit.visibility = View.VISIBLE
            layoutDelete.visibility = View.VISIBLE
        }
        else{
            layoutEdit.visibility= View.GONE
            layoutDelete.visibility= View.GONE
        }

        // ✅ Set inner list
        personAdapter.submitList(item.persons ?: emptyList())




        tvSNo.text = "S.No. ${position + 1}"
      //  noOfPerson.text = "Total Person  ${item.persons.size}"
        tvCustomerName.text = Html.fromHtml(
            "<b>Customer Name | </b> ${item.accountName}",
            Html.FROM_HTML_MODE_LEGACY
        )


        // ✅ Edit click
        layoutEdit.setOnClickListener {
            onItemEditClick?.invoke(item)
        }


        // ✅ Delete click
        layoutDelete.setOnClickListener {
            onItemDeleteClick?.invoke(item)
        }
    }
}