package com.ssspvtltd.quick_new.ui.order.add.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.ssspvtltd.quick_new.R
import com.ssspvtltd.quick_new.model.order.add.ItemsData

class ItemDataListAdapter(
    private val mContext: Context,
    private val mLayoutResourceId: Int,
    itemsDataList: List<ItemsData>,
) : ArrayAdapter<ItemsData>(mContext, mLayoutResourceId, itemsDataList) {

    private val itemsData: MutableList<ItemsData> = ArrayList(itemsDataList)
    private var allItemsData: List<ItemsData> = ArrayList(itemsDataList)

    override fun getCount(): Int {
        Log.e("itemsData.size",itemsData.size.toString())
        return itemsData.size
    }

    override fun getItem(position: Int): ItemsData {
        return itemsData[position]
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
            val item = getItem(position)
            val packAutoCompleteView =
                rowView.findViewById<View>(R.id.tvAccountNo) as TextView
                packAutoCompleteView.text = item.itemName.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
         return rowView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any): String? {
                return (resultValue as ItemsData).itemName.toString()
            }
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val itemSuggestion: MutableList<ItemsData> = ArrayList()
                    for (iName in allItemsData) {
                        if (iName.itemName.toString().replace(" ","").lowercase()
                                .contains(constraint.toString().replace(" ","").lowercase())
                        ) {
                            itemSuggestion.add(iName)
                        }
                    }
                    filterResults.values = itemSuggestion
                    filterResults.count = itemSuggestion.size
                }
                return filterResults
            }
            override fun publishResults(
                constraint: CharSequence?, results: FilterResults
            ) {
                itemsData.clear()
                if (results.count > 0) {
                    for (result in results.values as List<*>) {
                        if (result is ItemsData)    {
                            itemsData.add(result)
                        }
                    }
                    notifyDataSetChanged()
                } else if (constraint == null) {
                    itemsData.addAll(allItemsData)
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}
