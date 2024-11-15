package com.ssspvtltd.quick.model.order.pending

import android.os.Parcelable
import com.ssspvtltd.quick.base.recycler.data.BaseItem
import com.ssspvtltd.quick.base.recycler.data.BaseViewType
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.CommonViewType
import kotlinx.parcelize.Parcelize

@Parcelize
data class PendingOrderItem(
    val amount: String?,
    val imagePathList: List<ImagePath>,
    val itemNameList: List<ItemName>,
    val itemDetail: List<ItemName>,
    val orderID: String,
    val orderNo: String?,
    val qty: Int?,
    val salePartyEmail: String?,
    val salePartyMob: String?,
    val salePartyName: String?,
    val subPartyName: String?,
    val supplierMob: String?,
    val supplierName: String?,
    val status: String?,
    val isAdjustedStatus: Boolean?,
    val remark: String?,
    val subPartyasRemark: String?,
    val type: String?,
    val pdfPathList: List<PdfPath>
) : BaseWidget, Parcelable {
    override val viewType: BaseViewType get() = CommonViewType.DATA

    override fun getUniqueId(): String = orderID + orderNo

    @Parcelize
    data class ImagePath(
        val url: String
    ) : Parcelable

    @Parcelize
    data class ItemName(
        val amount: Int,
        val itemName: String,
        val qty: Int
    ) : Parcelable, BaseItem {
        override fun getUniqueId() = this.toString()
    }

    @Parcelize
    data class PdfPath(
       val pdfUrl: String?
    ) : Parcelable
}