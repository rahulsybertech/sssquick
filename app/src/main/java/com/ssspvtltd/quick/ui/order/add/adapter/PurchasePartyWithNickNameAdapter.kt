package com.ssspvtltd.quick.ui.order.add.adapter

import android.content.Context
import android.widget.ArrayAdapter
import com.ssspvtltd.quick.model.order.add.PurchasePartyData

class PurchasePartyWithNickNameAdapter (
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

    override fun getItem(position: Int): PurchasePartyData = purchaseParty[position]}
