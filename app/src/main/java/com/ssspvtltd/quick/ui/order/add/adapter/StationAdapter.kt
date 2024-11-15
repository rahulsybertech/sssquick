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
import com.ssspvtltd.quick.model.order.add.salepartydetails.AllStation


class StationAdapter(
    private val mContext: Context,
    private val mLayoutResourceId: Int,
    defStationList: List<AllStation>,
) : ArrayAdapter<AllStation>(mContext, mLayoutResourceId, defStationList) {

    private val defStation: MutableList<AllStation?> = ArrayList(defStationList)
    private var allDefStation: List<AllStation?> = ArrayList(defStationList)


    fun setdefStationList(defStationList: List<AllStation>) {
        defStation.clear()
        defStation.addAll(defStationList)
        allDefStation = ArrayList(defStationList)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        Log.e("countCC", defStation.size.toString())
        return defStation.size
    }

    override fun getItem(position: Int): AllStation? {
        return defStation[position]
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = mContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = convertView ?: inflater.inflate(R.layout.item_saleparty, parent, false)
        try {
            val station = getItem(position)
            val stationAutoCompleteView =
                rowView.findViewById<View>(R.id.tvAccountNo) as TextView
            stationAutoCompleteView.text = station?.stationName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rowView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any): String {
                return (resultValue as AllStation).stationName
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val stationNameSuggestion: MutableList<AllStation?> = ArrayList()
                    for (sName in allDefStation) {
                        if (sName?.stationName?.replace(" ","")?.lowercase()!!
                                .contains(constraint.toString().replace(" ","").lowercase())
                        ) {
                            stationNameSuggestion.add(sName)
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
                defStation.clear()
                if (results.count > 0) {
                    for (result in results.values as List<*>) {
                        if (result is AllStation?) {
                            defStation.add(result)
                        }
                    }
                    notifyDataSetChanged()
                } else if (constraint == null) {
                    defStation.addAll(allDefStation)
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}
