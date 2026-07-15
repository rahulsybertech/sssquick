package com.ssspvtltd.quick.ui.followupcall

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.databinding.AdapterFollowupCallBinding
import com.ssspvtltd.quick.ui.followupcall.LeadData
import com.ssspvtltd.quick.utils.DateTimeFormat
import com.ssspvtltd.quick.utils.DateTimeUtils

class FollowUpCallsAdapter : RecyclerView.Adapter<FollowUpCallsAdapter.PendingLrViewHolder>() {

    private val list = ArrayList<LeadData>()

    var onItemEditClick: ((LeadData, String, Int) -> Unit)? = null
    var onItemDeleteClick: ((LeadData) -> Unit)? = null
    var onRemarkFocus: ((Int) -> Unit)? = null

    var selectedType: String = "1"
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun submitList(newList: List<LeadData>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingLrViewHolder {
        val binding = AdapterFollowupCallBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PendingLrViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingLrViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class PendingLrViewHolder(
        private val binding: AdapterFollowupCallBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LeadData) = with(binding) {

            if (item.isFollowupDone) {
                btnFollowup.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(root.context, android.R.color.holo_blue_dark)
                )
            } else {
                btnFollowup.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(root.context, android.R.color.holo_green_dark)
                )
            }

            tvMobileNo.text = item.mobileNo
            tvLastYearSale.text = "₹${item.netAmt}"
            tvLastFollowupDate.text = item.lastFollowupDate

            if (selectedType == "3") {

                tvCustomerName.text = "Lead No. : "
                tvCustomerName1.text = item.leadNo

                tvSubPartyLabel.text = "Lead Name : "
                tvSubParty.text = item.accountName

                tvRemark.visibility = View.VISIBLE
                edtRemark.visibility = View.VISIBLE

                btnCall.visibility = View.GONE
                btnCallLead.visibility = View.VISIBLE
                btnFollowup.visibility = View.VISIBLE

                tvSaleLabel.visibility = View.GONE
                tvLastYearSale.visibility = View.GONE

                tvPartyType.visibility = View.GONE
                tvPartyTypeLabel.visibility = View.GONE

                tvClubType.visibility = View.GONE
                tvClubTypeLabel.visibility = View.GONE

                tvLeadDate.visibility = View.VISIBLE
                tvLeadDateLabel.visibility = View.VISIBLE

                tvLastFollowup.visibility = View.VISIBLE
                tvLastFollowupDate.visibility = View.VISIBLE

                tvLeadDate.text = DateTimeUtils.formatDate(
                    item.date,
                    DateTimeFormat.DATE_TIME_FORMAT5,
                    DateTimeFormat.DATE_TIME_FORMAT6
                )

            } else {

                tvCustomerName.text = "Cust Name : "
                tvCustomerName1.text = item.accountName

                tvSubPartyLabel.text = "Sub Party : "
                tvSubParty.text = item.subParty

                btnCall.visibility = View.VISIBLE
                btnCallLead.visibility = View.GONE
                btnFollowup.visibility = View.GONE

                tvRemark.visibility = View.GONE
                edtRemark.visibility = View.GONE

                tvSaleLabel.visibility = View.VISIBLE
                tvLastYearSale.visibility = View.VISIBLE

                tvPartyType.visibility = View.VISIBLE
                tvPartyTypeLabel.visibility = View.VISIBLE
                tvPartyType.text = item.partyType

                tvClubType.visibility = View.VISIBLE
                tvClubTypeLabel.visibility = View.VISIBLE
                tvClubType.text = item.clubType

                tvLeadDate.visibility = View.GONE
                tvLeadDateLabel.visibility = View.GONE

                tvLastFollowup.visibility = View.GONE
                tvLastFollowupDate.visibility = View.GONE
            }

            edtRemark.setText(item.remark)

            btnFollowup.setOnClickListener {
                item.isFollowupDone = true
                item.remark = edtRemark.text.toString()

                btnFollowup.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(root.context, android.R.color.holo_blue_dark)
                )

                onItemEditClick?.invoke(
                    item,
                    edtRemark.text.toString(),
                    bindingAdapterPosition
                )
            }

            btnCall.setOnClickListener {
                onItemDeleteClick?.invoke(item)
            }

            btnCallLead.setOnClickListener {
                onItemDeleteClick?.invoke(item)
            }
        }
    }
    fun clearList() {
        list.clear()
        notifyDataSetChanged()
    }
}