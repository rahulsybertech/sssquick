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
import com.ssspvtltd.quick.model.order.add.salepartydetails.SubParty


class SubPartyAdapter(
    private val mContext: Context,
    private val mLayoutResourceId: Int,
    subPartyData : List<SubParty>,
) : ArrayAdapter<SubParty>(mContext, mLayoutResourceId, subPartyData) {

    private var subParty: MutableList<SubParty> = ArrayList(subPartyData)
    private var allSubParty: List<SubParty> = ArrayList(subPartyData)

    fun setSubPartyData(newSubPartyData: List<SubParty>) {
        subParty.clear()
        subParty.addAll(newSubPartyData)
        allSubParty = ArrayList(newSubPartyData)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        Log.e("countCC", subParty.size.toString())
        return subParty.size
    }

    override fun getItem(position: Int): SubParty {
        return subParty[position]
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = mContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.item_saleparty, parent, false)
        try {
            val subPartyData = getItem(position)
            val subAutoCompleteView =
                rowView.findViewById<View>(R.id.tvAccountNo) as TextView
            subAutoCompleteView.text = subPartyData.subPartyName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rowView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any): String {
                return (resultValue as SubParty).subPartyName
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val subPartySuggestion: MutableList<SubParty> = ArrayList()
                    for (subParty in allSubParty) {
                        if (subParty.subPartyName?.replace(" ","")?.lowercase()!!
                                .contains(constraint.toString().replace(" ","").lowercase())
                        ) {
                            subPartySuggestion.add(subParty)
                        }
                    }
                    filterResults.values = subPartySuggestion
                    filterResults.count = subPartySuggestion.size
                }
                return filterResults
            }

            override fun publishResults(
                constraint: CharSequence?, results: FilterResults
            ) {
                subParty.clear()
                if (results.count > 0) {
                    for (result in results.values as List<*>) {
                        if (result is SubParty) {
                            subParty.add(result)
                        }
                    }
                    notifyDataSetChanged()
                } else if (constraint == null) {
                    subParty.addAll(allSubParty)
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}
