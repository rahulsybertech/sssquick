package com.ssspvtltd.quick.ui.followupcall.adapter

import android.content.res.ColorStateList
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.core.content.ContextCompat
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick.base.recycler.adapter.MultiViewAdapter
import com.ssspvtltd.quick.databinding.AdapterFollowupCallBinding
import com.ssspvtltd.quick.databinding.AdapterTourRegisterListBinding
import com.ssspvtltd.quick.ui.customerDetails.allAccount.PersonAdapter
import com.ssspvtltd.quick.ui.followupcall.LeadData
import com.ssspvtltd.quick.ui.tour.model.LeadItem
import com.ssspvtltd.quick.utils.CommaSparateAmount
import com.ssspvtltd.quick.utils.DateTimeFormat
import com.ssspvtltd.quick.utils.DateTimeUtils


class FollowUpCallsAdapter : MultiViewAdapter() {
    internal var onItemEditClick: ((LeadData,remark:String,position: Int) -> Unit)? = null
    internal var onItemDeleteClick: ((LeadData) -> Unit)? = null
    var onRemarkFocus: ((Int) -> Unit)? = null
    var selectedType: String = "1"
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = AdapterFollowupCallBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingLrViewHolder(
            binding,
            onItemEditClick,
            onItemDeleteClick,onRemarkFocus
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is PendingLrViewHolder -> {
                getItemOrNull<LeadData>(position)?.let {
                    holder.bind(it, position,selectedType)
                }
            }

            else -> super.onBindViewHolder(holder, position)
        }
    }
}
class PendingLrViewHolder(
    private val binding: AdapterFollowupCallBinding,
    private val onItemEditClick: ((LeadData,remark:String,position: Int) -> Unit)?,
    private val onItemDeleteClick: ((LeadData) -> Unit)?,
    private val onRemarkFocus: ((position: Int) -> Unit)?
) : BaseViewHolder(binding) {

    private val personAdapter = PersonAdapter()

    fun bind(item: LeadData,position: Int,  selectedType: String) = with(binding) {


        //  noOfPerson.text = "Total Person  ${item.persons.size}"

        if (item.isFollowupDone) {
            btnFollowup.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(root.context, android.R.color.holo_blue_dark)
            )

        } else {
            btnFollowup.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(root.context, android.R.color.holo_green_dark)
            )
        }
        tvMobileNo.text=item.mobileNo
        tvLastYearSale.text=CommaSparateAmount.formatIndianAmount(item.netAmt)

        tvLastFollowupDate.text = DateTimeUtils.formatDate(
            item.lastFollowupDate,
            DateTimeFormat.DATE_TIME_FORMAT5,
            DateTimeFormat.DATE_TIME_FORMAT6
        )

        if(selectedType == "3"){
            tvCustomerName1.text = item.leadNo
            tvCustomerName.setText("Lead No. : ")
            tvSubPartyLabel.setText("Lead Name : ")
            tvSubParty.text=item.accountName
            tvRemark.visibility= View.VISIBLE
            btnCall.visibility= View.INVISIBLE
            btnCallLead.visibility= View.VISIBLE
            edtRemark.visibility= View.VISIBLE
            tvLastYearSale.visibility= View.GONE
            tvSaleLabel.visibility= View.GONE
            edtRemark.setText(item.remark)
            btnFollowup.visibility= View.VISIBLE

            tvLastFollowup.visibility= View.VISIBLE
            tvLastFollowupDate.visibility= View.VISIBLE
            tvPartyTypeLabel.visibility= View.GONE
            tvPartyType.visibility= View.GONE
            tvClubTypeLabel.visibility= View.GONE
            tvClubType.visibility= View.GONE
            tvLeadDateLabel.visibility= View.VISIBLE
            tvLeadDate.visibility= View.VISIBLE

            tvLeadDate.text = DateTimeUtils.formatDate(
                item.date,
                DateTimeFormat.DATE_TIME_FORMAT5,
                DateTimeFormat.DATE_TIME_FORMAT6
            )

        }else{

            tvCustomerName.setText("Cust Name : ")
            tvCustomerName1.text = item.accountName
            tvSubPartyLabel.setText("Sub Party : ")
            tvSubParty.text=item.subParty
            btnCall.visibility= View.VISIBLE
            btnCallLead.visibility= View.INVISIBLE
            tvRemark.visibility= View.GONE
            tvLastYearSale.visibility= View.VISIBLE
            tvSaleLabel.visibility= View.VISIBLE
            edtRemark.visibility= View.GONE
            edtRemark.setText(item.remark)
            btnFollowup.visibility= View.GONE

            tvLastFollowup.visibility= View.GONE
            tvLastFollowupDate.visibility= View.GONE
            tvPartyTypeLabel.visibility= View.VISIBLE
            tvPartyType.visibility= View.VISIBLE
            tvPartyType.text=item.partyType
            tvClubTypeLabel.visibility= View.VISIBLE
            tvClubType.visibility= View.VISIBLE
            tvClubType.text=item.clubType
            tvLeadDateLabel.visibility= View.GONE
            tvLeadDate.visibility= View.GONE
        }

        edtRemark.setText(item.remark)
    /*    edtRemark.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                onRemarkFocus?.invoke(position)
            }
        }*/


        // ✅ Edit click
        btnFollowup.setOnClickListener {
            item.isFollowupDone = true
            item.remark = edtRemark.text.toString()
            btnFollowup.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(root.context, android.R.color.holo_blue_dark)
            )

            onItemEditClick?.invoke(item, edtRemark.text.toString(), position)
        }
        btnCall.setOnClickListener {
            onItemDeleteClick?.invoke(item)
        }
        btnCallLead.setOnClickListener {
            onItemDeleteClick?.invoke(item)
        }

    }
}