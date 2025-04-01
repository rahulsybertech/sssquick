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
import com.ssspvtltd.quick.model.order.add.DispatchTypeList
import com.ssspvtltd.quick.model.order.add.SchemeData

class DispatchAdapter (
    private val mContext: Context,
    private val mLayoutResourceId: Int,
    schemeDataList: List<DispatchTypeList>,
) : ArrayAdapter<DispatchTypeList>(mContext, mLayoutResourceId, schemeDataList) {

    private val schemeData: MutableList<DispatchTypeList> = ArrayList(schemeDataList)
    private var allSchemeData: List<DispatchTypeList> = ArrayList(schemeDataList)

    fun setSchemeData(schemeDataList: List<DispatchTypeList>) {
        schemeData.clear()
        schemeData.addAll(schemeDataList)
        allSchemeData = ArrayList(schemeDataList)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        Log.e("countCC", schemeData.size.toString())
        return schemeData.size
    }

    override fun getItem(position: Int): DispatchTypeList {
        return schemeData[position]
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.item_saleparty, parent, false)
        try {
            val scheme = getItem(position)
            val schemeAutoCompleteView = rowView.findViewById<View>(R.id.tvAccountNo) as TextView
            schemeAutoCompleteView.text = scheme.value
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rowView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any): String? {
                return (resultValue as DispatchTypeList).value
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val schemeNameSuggestion: MutableList<DispatchTypeList> = ArrayList()
                    for (sName in allSchemeData) {
                        if (sName.value?.replace(" ","")?.lowercase()!!
                                .contains(constraint.toString().replace(" ","").lowercase())
                        ) {
                            schemeNameSuggestion.add(sName)
                        }
                    }
                    filterResults.values = schemeNameSuggestion
                    filterResults.count = schemeNameSuggestion.size
                }
                return filterResults
            }
            override fun publishResults(
                constraint: CharSequence?, results: FilterResults
            ) {
                schemeData.clear()
                if (results.count > 0) {
                    for (result in results.values as List<*>) {
                        if (result is DispatchTypeList) {
                            schemeData.add(result)
                        }
                    }
                    notifyDataSetChanged()
                } else if (constraint == null) {
                    schemeData.addAll(allSchemeData)
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}
