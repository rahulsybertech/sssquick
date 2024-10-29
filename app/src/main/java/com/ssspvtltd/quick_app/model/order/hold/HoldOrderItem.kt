package com.ssspvtltd.quick_app.model.order.hold

import android.os.Parcelable
import com.ssspvtltd.quick_app.base.recycler.data.BaseItem
import com.ssspvtltd.quick_app.base.recycler.data.BaseViewType
import com.ssspvtltd.quick_app.base.recycler.data.BaseWidget
import com.ssspvtltd.quick_app.base.recycler.data.CommonViewType
import kotlinx.parcelize.Parcelize

@Parcelize
data class HoldOrderItem(
    val amount: Double?,
    val imagePathList: List<ImagePath>?,
    val itemNameList: List<ItemName>?,
    val itemDetail: List<ItemName>?,
    val orderID: String?,
    val orderNo: String?,
    val qty: Int?,
    val salePartyEmail: String?,
    val salePartyMob: String?,
    val salePartyName: String,
    val subPartyName: String?,
    val supplierMob: String?,
    val supplierName: String?,
    val status: String?,
    val remark: String?,
    val type: String?
): BaseWidget, Parcelable {
    override val viewType: BaseViewType get() = CommonViewType.DATA

    override fun getUniqueId(): String = orderID + orderNo

    @Parcelize
    data class ImagePath(
        val url: String?
    ) : Parcelable

    @Parcelize
    data class ItemName(
        val amount: Int?,
        val itemName: String?,
        val qty: Int?
    ) : Parcelable , BaseItem {
        override fun getUniqueId() = this.toString()
    }
}