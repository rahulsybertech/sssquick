package com.ssspvtltd.quick.ui.order.goodsreturn.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.recyclerview.widget.RecyclerView
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.model.GoodsReturnItem
import com.ssspvtltd.quick.model.grNew.GoodsReturnDataNew
import com.ssspvtltd.quick.model.order.goodsreturn.GoodsReturnData
import com.ssspvtltd.quick.ui.order.goodsreturn.fragment.GoodsReturnFragmentNew
import com.ssspvtltd.quick.utils.DateTimeFormat
import com.ssspvtltd.quick.utils.DateTimeUtils

class GoodsReturnAdapterNew(
    private val onItemClick: (com.ssspvtltd.quick.model.grNew.GoodsReturnItem) -> Unit
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list = mutableListOf<Any>()

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    fun setData(data: List<GoodsReturnDataNew>) {
        list.clear()
        data.forEach { group ->
            list.add(group) // add header
            if (group.isExpanded) {
                list.addAll(group.goodsReturnDetailList) // add children
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is GoodsReturnDataNew -> TYPE_HEADER
            is com.ssspvtltd.quick.model.grNew.GoodsReturnItem -> TYPE_ITEM
            else -> throw IllegalStateException("Invalid item")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_goods_return_headernew, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_goods_return, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            val group = list[position] as GoodsReturnDataNew
            holder.bind(group)
        } else if (holder is ItemViewHolder) {
            val item = list[position] as com.ssspvtltd.quick.model.grNew.GoodsReturnItem
            holder.bind(item)
        }
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvBillDate: TextView = view.findViewById(R.id.tvOrderDate)
        private val tvSaleBillNo: TextView = view.findViewById(R.id.tvSaleBillNo)
        private val tvSaleParty: TextView = view.findViewById(R.id.tvSaleParty)
        private val tvSupplier: TextView = view.findViewById(R.id.tvSupplier)
        private val ivArrow: ImageView = view.findViewById(R.id.ivArrow)

        fun bind(group: GoodsReturnDataNew) {

            tvBillDate.text = "Date |"+ DateTimeUtils.formatDate(
                group.billDate, DateTimeFormat.DATE_TIME_FORMAT1,
                DateTimeFormat.DATE_TIME_FORMAT3
            )
            tvSaleBillNo.text = "Sale Bill No | "+group.saleBillNo
            tvSaleParty.text = "Sale Party Name | "+group.salePartyName
            tvSupplier.text = "Supplier Name | "+group.supplierName
            ivArrow.setImageResource(
                if (group.isExpanded) R.drawable.ic_up_arrow
                else R.drawable.ic_drop_arrow
            )
           // ivArrow.rotation = if (group.isExpanded) 180f else 0f

            itemView.setOnClickListener {
                val currentIndex = collectGroups().indexOf(group)

                // Collapse all groups
                collectGroups().forEach { it.isExpanded = false }

                // Expand only current
                group.isExpanded = true

                // Rebuild list with only one expanded
                setData(collectGroups())
            }
        }
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvItemName: TextView = view.findViewById(R.id.tvItemName)
        private val tvSaleBillNo: TextView = view.findViewById(R.id.tvSaleBillNo)
        private val tvSaleParty: TextView = view.findViewById(R.id.tvSaleParty)
        private val tvSupplier: TextView = view.findViewById(R.id.tvSupplier)
        private val tvOrderStatus: TextView = view.findViewById(R.id.tvOrderStatus)
        private val tvSubParty: TextView = view.findViewById(R.id.tvSubParty)
        private val tvOrderAmount: TextView = view.findViewById(R.id.tvOrderAmount)
        private val imgGo: ImageView = view.findViewById(R.id.imgGo)
    //    private val tvOrderAmount: ImageView = view.findViewById(R.id.tvOrderAmount)
       // private val tvQty: TextView = view.findViewById(R.id.tvQty)

        fun bind(item: com.ssspvtltd.quick.model.grNew.GoodsReturnItem) {
            tvItemName.text = ""+item.itemName
            tvSaleBillNo.text = "Sale Bill No |"+item.saleBillNo
            tvSaleParty.text = "Sale Party Name |"+item.salePartyName
            tvSupplier.text = "Supplier Name |"+item.supplierName
            tvSupplier.text = "Supplier Name |"+item.supplierName
            tvItemName.text = item.itemName
            tvSubParty.text ="Qty: ${(item.qty ?: " - ")}"
            tvOrderAmount.text = item.amount.toString()?:"0"
            // tvItemName.text = "Jeans"
            tvOrderStatus.text = item.status?:"Pending"

           // tvSubParty.text = item.status
       //     tvOrderStatus.text = item.
       //     tvQty.text = "Qty: ${item.qty}"

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    private fun collectGroups(): List<GoodsReturnDataNew> {
        return list.filterIsInstance<GoodsReturnDataNew>()
    }
}

