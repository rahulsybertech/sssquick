package com.ssspvtltd.quick_app.ui.order.add.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.ssspvtltd.quick_app.R
import com.ssspvtltd.quick_app.model.order.add.PackTypeData


class PackTypeAdapter(
    private val mContext: Context,
    private val mLayoutResourceId: Int,
    packTypeDataList: List<PackTypeData>,
) : ArrayAdapter<PackTypeData>(mContext, mLayoutResourceId, packTypeDataList) {

    private val packType: MutableList<PackTypeData> = ArrayList(packTypeDataList)
    private var allPackType: List<PackTypeData> = ArrayList(packTypeDataList)

    override fun getCount(): Int {
        return packType.size
    }

    override fun getItem(position: Int): PackTypeData {
        return packType[position]
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = mContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.item_saleparty, parent, false)
        try {
            val packTypeData = getItem(position)
            val packAutoCompleteView =
                rowView.findViewById<View>(R.id.tvAccountNo) as TextView
            packAutoCompleteView.text = packTypeData.value.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rowView!!
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any): String? {
                return (resultValue as PackTypeData).value.toString()
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val packPartySuggestion: MutableList<PackTypeData> = ArrayList()
                    for (pType in allPackType) {
                        if (pType.value.toString().replace(" ","").lowercase()
                                .contains(constraint.toString().replace(" ","").lowercase())
                        ) {
                            packPartySuggestion.add(pType)
                        }
                    }
                    filterResults.values = packPartySuggestion
                    filterResults.count = packPartySuggestion.size
                }
                return filterResults
            }
            override fun publishResults(
                constraint: CharSequence?, results: FilterResults
            ) {
                packType.clear()
                if (results.count > 0) {
                    for (result in results.values as List<*>) {
                        if (result is PackTypeData) {
                            packType.add(result)
                        }
                    }
                    notifyDataSetChanged()
                } else if (constraint == null) {
                    packType.addAll(allPackType)
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}