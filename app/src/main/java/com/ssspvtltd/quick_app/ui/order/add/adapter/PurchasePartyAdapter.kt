package com.ssspvtltd.quick_app.ui.order.add.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.ssspvtltd.quick_app.R
import com.ssspvtltd.quick_app.model.order.add.PurchasePartyData


class PurchasePartyAdapter(
    private val type: Boolean,
    private val mContext: Context,
    private val mLayoutResourceId: Int,
    purchasePartyData: List<PurchasePartyData>,
) : ArrayAdapter<PurchasePartyData>(mContext, mLayoutResourceId, purchasePartyData) {

    private val purchaseParty: MutableList<PurchasePartyData> = ArrayList(purchasePartyData)
    private var allPurchaseParty: List<PurchasePartyData> = ArrayList(purchasePartyData)


    override fun getCount(): Int {
        Log.e("countCC", purchaseParty.size.toString())
        return purchaseParty.size
    }

    override fun getItem(position: Int): PurchasePartyData {
        return purchaseParty[position]
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = mContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.item_saleparty, parent, false)
        try {
            val purchaseParty = getItem(position)
            val saleAutoCompleteView =
                rowView.findViewById<View>(R.id.tvAccountNo) as TextView
            if (!type)
                saleAutoCompleteView.text = purchaseParty.accountName
            else
                saleAutoCompleteView.text = purchaseParty.nickName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rowView!!
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any): String? {
                return if(!type){
                    (resultValue as PurchasePartyData).accountName
                }else{
                    (resultValue as PurchasePartyData).nickName
                }
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val purchasePartySuggestion: MutableList<PurchasePartyData> = ArrayList()
                    for (pParty in allPurchaseParty) {
                        if (pParty.accountName?.replace(" ","")?.lowercase()!!
                                .contains(constraint.toString().replace(" ","").lowercase()) || pParty.accountName.replace(" ","")?.lowercase()!!
                                .contains(constraint.toString().replace(" ","").lowercase())
                        ) {
                            purchasePartySuggestion.add(pParty)
                        }
                    }
                    filterResults.values = purchasePartySuggestion
                    filterResults.count = purchasePartySuggestion.size
                }
                return filterResults
            }

            override fun publishResults(
                constraint: CharSequence?, results: FilterResults
            ) {
                purchaseParty.clear()
                if (results.count > 0) {
                    for (result in results.values as List<*>) {
                        if (result is PurchasePartyData) {
                            purchaseParty.add(result)
                        }
                    }
                    notifyDataSetChanged()
                } else if (constraint == null) {
                    purchaseParty.addAll(allPurchaseParty)
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}
