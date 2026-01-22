package com.ssspvtltd.quick.ui.mailboxremark
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick.base.recycler.adapter.MultiViewAdapter
import com.ssspvtltd.quick.base.recycler.data.CommonViewType
import com.ssspvtltd.quick.base.recycler.data.TitleSubtitleWrapper
import com.ssspvtltd.quick.databinding.MailBoxBinding
import com.ssspvtltd.quick.databinding.MailBoxHeaderBinding
import com.ssspvtltd.quick.model.mailbox.MailDetail
import com.ssspvtltd.quick.utils.CommaSparateAmount
import com.ssspvtltd.quick.utils.DateTimeFormat
import com.ssspvtltd.quick.utils.DateTimeUtils

class MailBoxAdapter : MultiViewAdapter() {
    internal var onItemClick: ((MailDetail) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            CommonViewType.HEADER.id -> {
                val binding = MailBoxHeaderBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return GoodsReturnHeaderViewHolder(binding)
            }

            CommonViewType.DATA.id -> {
                val binding = MailBoxBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return GoodsReturnViewHolder(binding).apply {
                    itemView.setOnClickListener {
                        getItemOrNull<MailDetail>(bindingAdapterPosition)?.let {
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
                getItemOrNull<MailDetail>(position)?.let(holder::bind)
            }

            else -> super.onBindViewHolder(holder, position)
        }
    }
}

class GoodsReturnHeaderViewHolder(private val binding: MailBoxHeaderBinding) :
    BaseViewHolder(binding) {
    fun bind(item: TitleSubtitleWrapper) = with(binding) {
        tvOrderDate.text = "Bill Date: "+DateTimeUtils.formatDate(
            item.title, DateTimeFormat.DATE_TIME_FORMAT1,
            DateTimeFormat.DATE_TIME_FORMAT3)
/*        tvOrderDate.text=getString(R.string.billDate,DateTimeUtils.formatDate(
            item.title, DateTimeFormat.DATE_TIME_FORMAT1,
            DateTimeFormat.DATE_TIME_FORMAT3))*/
    }
}

class GoodsReturnViewHolder(private val binding: MailBoxBinding) :
    BaseViewHolder(binding) {
    fun bind(item: MailDetail) = with(binding) {
        orderDate.text=getString(R.string.order_date_,DateTimeUtils.formatDate(
            item.orderDate, DateTimeFormat.DATE_TIME_FORMAT1,
            DateTimeFormat.DATE_TIME_FORMAT3))
        mailDate.text=getString(R.string.mailDate,DateTimeUtils.formatDate(
            item.mailDate, DateTimeFormat.DATE_TIME_FORMAT1,
            DateTimeFormat.DATE_TIME_FORMAT2))
        orderNo.text = getString(R.string.orderNo, item.orderNo)
/*        purchaseBillNo.apply {
            text = getString(R.string.purchaseBillNo, item.purchaseBillNo)
            setTextColor(Color.parseColor("#1A0DAB")) // hyperlink blue
            paint.isUnderlineText = true
            setOnClickListener {
                item.filePath?.let { pdf ->
                    openPdf(pdf)
                }
            }
        }*/

        val label = "P.Bill.N: "
        val billNo = item.purchaseBillNo ?: ""

        val spannable = SpannableString("$label$billNo")

// Apply hyperlink style ONLY on billNo part
        val startIndex = label.length
        val endIndex = label.length + billNo.length

        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#1A0DAB")),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            UnderlineSpan(),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

// Set click action
        purchaseBillNo.text = spannable
        purchaseBillNo.movementMethod = LinkMovementMethod.getInstance()

        purchaseBillNo.setOnClickListener {
            item.filePath?.let { pdf -> openPdf(pdf) }
        }

        tvSaleBillNo.text = getString(R.string.saleParty, item.saleParty)
        item.remark
            .takeIf { it.isNotBlank() }
            ?.let {
                remark.text = getString(R.string.remark_, it)
                remark.visibility = View.VISIBLE
            }
            ?: run {
                remark.visibility = View.GONE
            }

        tvSaleParty.text = getString(R.string.purchaseParty, item.purchaseParty)
        tvSupplier.text = getString(R.string.netAmt, CommaSparateAmount.formatIndianAmount(item.netAmt))
        // tvSubParty.text = item.subPartyName?:"Self"
/*
        tvSubParty.text ="Qty: ${(item.qty ?: " - ")}"
        // tvItemName.text = "Jeans"
        tvOrderStatus.text = item.status?:"Pending"
        tvOrderAmount.text = getString(R.string.amount_format,item.amount)
   */ }

    private fun openPdf(url: String) {
        val context = binding.root.context
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(url), "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            context. startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show()
        }
    }
}
