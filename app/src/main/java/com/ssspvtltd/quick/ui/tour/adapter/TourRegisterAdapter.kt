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
import com.ssspvtltd.quick.ui.tour.model.LeadItem

class TourRegisterAdapter    : MultiViewAdapter() {
    internal var onItemEditClick: ((LeadItem) -> Unit)? = null
    internal var onItemDeleteClick: ((LeadItem) -> Unit)? = null
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
                getItemOrNull<LeadItem>(position)?.let {
                    holder.bind(it, position)
                }
            }

            else -> super.onBindViewHolder(holder, position)
        }
    }
}
class PendingLrViewHolder(
    private val binding: AdapterTourRegisterListBinding,
    private val onItemEditClick: ((LeadItem) -> Unit)?,
    private val onItemDeleteClick: ((LeadItem) -> Unit)?
) : BaseViewHolder(binding) {

    private val personAdapter = PersonAdapter()

    fun bind(item: LeadItem,position: Int) = with(binding) {


     /*   if (item.editAllowed) {

            layoutEdit.visibility = View.VISIBLE
            layoutDelete.visibility = View.VISIBLE
        }
        else{
            layoutEdit.visibility= View.GONE
            layoutDelete.visibility= View.GONE
        }*/

        // ✅ Set inner list
       // personAdapter.submitList(item.persons ?: emptyList())




        tvSNo.text = "S.No. ${position + 1}"
      //  noOfPerson.text = "Total Person  ${item.persons.size}"
        tvCustomerName.text = Html.fromHtml(
            "<b>Firm Name | </b> ${item.firmName}",
            Html.FROM_HTML_MODE_LEGACY
        )
        tvCategory.text = Html.fromHtml(
            "<b>Category | </b> ${item.categoryName}",
            Html.FROM_HTML_MODE_LEGACY
        )
        tvGrade.text = Html.fromHtml(
            "<b>Mobile No | </b> ${item.mobileNo}",
            Html.FROM_HTML_MODE_LEGACY
        )
        tvLedNo.text = Html.fromHtml(
            "<b>Lead No. </b> ${item.leadNo}",
            Html.FROM_HTML_MODE_LEGACY
        )
        tvState.text = Html.fromHtml(
            "<b>State | </b> ${item.stateName}",
            Html.FROM_HTML_MODE_LEGACY
        )
        if (item.stationName == "OTHER") {
            tvStation.text = Html.fromHtml(
                "<b>Station | </b> ${item.station_Name.orEmpty()}",
                Html.FROM_HTML_MODE_LEGACY
            )
        } else {
            tvStation.text = Html.fromHtml(
                "<b>Station | </b> ${item.stationName.orEmpty()}",
                Html.FROM_HTML_MODE_LEGACY
            )
        }



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