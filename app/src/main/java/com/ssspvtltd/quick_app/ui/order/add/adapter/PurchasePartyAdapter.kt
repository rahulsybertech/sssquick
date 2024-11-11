package com.ssspvtltd.quick_app.ui.order.add.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.ssspvtltd.quick_app.R
import com.ssspvtltd.quick_app.model.order.add.PurchasePartyData


class PurchasePartyAdapter(
    private var type: Boolean,
    private val mContext: Context,
    private val mLayoutResourceId: Int,
    purchasePartyData: List<PurchasePartyData>,
) : ArrayAdapter<PurchasePartyData>(mContext, mLayoutResourceId, purchasePartyData) {

    private val purchaseParty: MutableList<PurchasePartyData> = ArrayList(purchasePartyData)
    private var allPurchaseParty: List<PurchasePartyData> = ArrayList(purchasePartyData)

    // Method to update the list of items dynamically
    fun updateData(newData: List<PurchasePartyData>) {
        purchaseParty.clear()
        purchaseParty.addAll(newData)
        allPurchaseParty = ArrayList(newData)
        notifyDataSetChanged()
    }

    // Method to update the type dynamically
    fun updateType(newType: Boolean) {
        type = newType
        notifyDataSetChanged()
    }

    override fun getCount(): Int = purchaseParty.size

    override fun getItem(position: Int): PurchasePartyData = purchaseParty[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = convertView ?: inflater.inflate(mLayoutResourceId, parent, false)

        val purchaseParty = getItem(position)
        val saleAutoCompleteView = rowView.findViewById<TextView>(R.id.tvAccountNo)

        // Display nickName or accountName based on `type`
        saleAutoCompleteView.text = if (type) purchaseParty.nickName else purchaseParty.accountName
        return rowView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any): String {
                val data = resultValue as PurchasePartyData
                return if (type) data.nickName ?: "" else data.accountName ?: ""
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val purchasePartySuggestion = allPurchaseParty.filter {
                        if (type) {
                            it.nickName?.contains(constraint, ignoreCase = true) == true
                        } else {
                            it.accountName?.contains(constraint, ignoreCase = true) == true
                        }
                    }
                    filterResults.values = purchasePartySuggestion
                    filterResults.count = purchasePartySuggestion.size
                } else {
                    filterResults.values = allPurchaseParty
                    filterResults.count = allPurchaseParty.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                purchaseParty.clear()
                if (results.values != null) {
                    @Suppress("UNCHECKED_CAST")
                    purchaseParty.addAll(results.values as List<PurchasePartyData>)
                    notifyDataSetChanged()
                } else if (constraint == null) {
                    purchaseParty.addAll(allPurchaseParty)
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}