package com.ssspvtltd.quick.ui.order.add.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.model.order.add.PurchasePartyData


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

    // fun updateData(newData: List<PurchasePartyData>) {
    //     // Create a set to ensure uniqueness based on the condition
    //     val uniqueData = if (type) {
    //         newData.distinctBy { it.nickName }
    //     } else {
    //         newData.distinctBy { it.accountName }
    //     }
    //
    //     purchaseParty.clear()
    //     purchaseParty.addAll(uniqueData)
    //     allPurchaseParty = ArrayList(uniqueData)
    //     notifyDataSetChanged()
    // }


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
        val str = purchaseParty.nickName

        println("CHECKING_DATA $str")
        // Display nickName or accountName based on `type'


        saleAutoCompleteView.text = if (type){
            purchaseParty.nickName
        } else{
            purchaseParty.accountName
        }
        return rowView
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