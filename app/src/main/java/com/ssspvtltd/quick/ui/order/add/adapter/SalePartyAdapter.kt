package com.ssspvtltd.quick.ui.order.add.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.model.order.add.SalepartyData


class SalePartyAdapter(
    private val mContext: Context,
    private val mLayoutResourceId: Int,
    customerData: List<SalepartyData>,
) : ArrayAdapter<SalepartyData>(mContext, mLayoutResourceId, customerData) {

    private var saleParty: MutableList<SalepartyData> = ArrayList(customerData)
    private var allSaleParty: List<SalepartyData> = ArrayList(customerData)


    override fun getCount(): Int {
        Log.e("countCC", saleParty.size.toString())
        return saleParty.size
    }

    fun setItem(newData:  List<SalepartyData>?) {
        saleParty.clear()
        saleParty.addAll(newData ?: emptyList())
        allSaleParty = ArrayList(newData ?: emptyList())

        notifyDataSetChanged()
    }

    override fun getItem(position: Int): SalepartyData {
        return saleParty[position]
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = mContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(
            R.layout.item_saleparty, parent, false
        )
        try {
            val customerData = getItem(position)
            val saleAutoCompleteView =
                rowView.findViewById<View>(R.id.tvAccountNo) as TextView
            saleAutoCompleteView.text = customerData.accountName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rowView!!
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any): String? {
                return (resultValue as SalepartyData).accountName
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val salePartySuggestion: MutableList<SalepartyData> = ArrayList()
                    for (saleParty in allSaleParty) {
                        Log.e("saleParty.accountName",saleParty.accountName?.replace(" ","").toString())
                        if (saleParty.accountName?.replace(" ","")?.lowercase()!!
                                .contains(constraint.toString().replace(" ","").lowercase())
                        ) {
                            salePartySuggestion.add(saleParty)
                        }
                    }
                    filterResults.values = salePartySuggestion
                    filterResults.count = salePartySuggestion.size
                }
                return filterResults
            }

            override fun publishResults(
                constraint: CharSequence?, results: FilterResults
            ) {
                saleParty.clear()
                if (results.count > 0) {
                    for (result in results.values as List<*>) {
                        if (result is SalepartyData) {
                            saleParty.add(result)
                        }
                    }
                    notifyDataSetChanged()
                } else if (constraint == null) {
                    saleParty.addAll(allSaleParty)
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}
