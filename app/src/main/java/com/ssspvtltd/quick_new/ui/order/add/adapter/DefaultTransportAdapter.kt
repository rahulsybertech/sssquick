package com.ssspvtltd.quick_new.ui.order.add.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.ssspvtltd.quick_new.R
import com.ssspvtltd.quick_new.model.order.add.salepartydetails.DefTransport


class DefaultTransportAdapter(
    private val mContext: Context,
    private val mLayoutResourceId: Int,
    defTransportList : List<DefTransport?>,
) : ArrayAdapter<DefTransport?>(mContext, mLayoutResourceId, defTransportList) {

    private val defTransport: MutableList<DefTransport?> = ArrayList(defTransportList)
    private var allDefTransport: List<DefTransport?> = ArrayList(defTransportList)


    override fun getCount(): Int {
        Log.e("countCC",defTransport.size.toString())
        return defTransport.size
    }

    override fun getItem(position: Int): DefTransport? {
        return defTransport[position]
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = mContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.item_saleparty, parent, false)
        try {
            val transport = getItem(position)
            val transportAutoCompleteView =
                rowView.findViewById<View>(R.id.tvAccountNo) as TextView
            transportAutoCompleteView.text = transport?.transportName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rowView!!
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any): String? {
                return (resultValue as DefTransport).transportName
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val stationNameSuggestion: MutableList<DefTransport?> = ArrayList()
                    for (tName in allDefTransport) {
                        if (tName?.transportName?.replace(" ","")?.lowercase()!!
                                .contains(constraint.toString().replace(" ","").lowercase())
                        ) {
                            stationNameSuggestion.add(tName)
                        }
                    }
                    filterResults.values = stationNameSuggestion
                    filterResults.count = stationNameSuggestion.size
                }
                return filterResults
            }

            override fun publishResults(
                constraint: CharSequence?, results: FilterResults
            ) {
                defTransport.clear()
                if (results.count > 0) {
                    for (result in results.values as List<*>) {
                        if (result is DefTransport?) {
                            defTransport.add(result)
                        }
                    }
                    notifyDataSetChanged()
                } else if (constraint == null) {
                    defTransport.addAll(allDefTransport)
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}
