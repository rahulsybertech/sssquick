package com.ssspvtltd.quick.model.order.stockinoffice

import android.os.Parcelable
import com.ssspvtltd.quick.base.recycler.data.BaseViewType
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.CommonViewType
import kotlinx.parcelize.Parcelize

@Parcelize
data class StockInOfficeOrderItem(
    val amount: Int,
    val imagePaths: String?,
    val orderDate: String?,
    val orderID: String,
    val purchaseNo: String,
    val qty: Int,
    val salePartyEmail: String,
    val salePartyMob: String?,
    val salePartyName: String,
    val subPartyName: String?,
    val supplierMob: String?,
    val supplierName: String,
    val type: String
): BaseWidget, Parcelable {
    override val viewType: BaseViewType get() = CommonViewType.DATA
    override fun getUniqueId(): String = orderID + purchaseNo
}