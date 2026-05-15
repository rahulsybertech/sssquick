package com.ssspvtltd.quick.ui.customerDetails.adapter
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ssspvtltd.quick.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick.base.recycler.adapter.MultiViewAdapter
import com.ssspvtltd.quick.databinding.AdapterCustomerListByCustomerCodeBinding
import com.ssspvtltd.quick.model.customerdetails.CustomerList
import com.ssspvtltd.quick.ui.customerDetails.allAccount.PersonAdapter
import com.ssspvtltd.quick.ui.customerDetails.model.CreateData
import com.ssspvtltd.quick.utils.DateTimeFormat
import com.ssspvtltd.quick.utils.DateTimeUtils

class CustomerListByCustomerCodeAdapter : MultiViewAdapter() {
    internal var onItemEditClick: ((CreateData) -> Unit)? = null
    internal var onItemDeleteClick: ((CreateData) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = AdapterCustomerListByCustomerCodeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerListByCustomerCodeViewHolder(
            binding,
            onItemEditClick,
            onItemDeleteClick
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is CustomerListByCustomerCodeViewHolder -> {
                getItemOrNull<CreateData>(position)?.let {
                    holder.bind(it, position)
                }
            }

            else -> super.onBindViewHolder(holder, position)
        }
    }
}
class CustomerListByCustomerCodeViewHolder(
    private val binding: AdapterCustomerListByCustomerCodeBinding,
    private val onItemEditClick: ((CreateData) -> Unit)?,
    private val onItemDeleteClick: ((CreateData) -> Unit)?
) : BaseViewHolder(binding) {



    fun bind(item: CreateData,position: Int) = with(binding) {

        tvSNo.text = "S.No. ${position + 1}"
        tvCustomerName.text = Html.fromHtml(
            "<b>Customer Name | </b> ${item.accountName}",
            Html.FROM_HTML_MODE_LEGACY
        )

        tvMarketerName.text = Html.fromHtml(
            "<b>Marketer Name | </b> ${item.marketerName}",
            Html.FROM_HTML_MODE_LEGACY
        )


        tvTotalPerson.text = Html.fromHtml(
            "<b>Total Person | </b> ${item.personCount}",
            Html.FROM_HTML_MODE_LEGACY
        )
        tvTotalPerson.text = Html.fromHtml(
            "<b>Total Person | </b> ${item.personCount}",
            Html.FROM_HTML_MODE_LEGACY
        )
        val cleanDate = item.date.replace("\\s+".toRegex(), " ")

      /*  val date = DateTimeUtils.formatDate(
            cleanDate,
            DateTimeFormat.DATE_TIME_FORMAT4,
            DateTimeFormat.DATE_TIME_FORMAT3
        )*/

        tvDate.text = Html.fromHtml(
            "<b>Created Date | </b> $cleanDate",
            Html.FROM_HTML_MODE_LEGACY
        )


    }
}