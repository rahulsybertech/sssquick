package com.ssspvtltd.quick.model.order.pendinglr

import android.os.Parcelable
import com.ssspvtltd.quick.base.recycler.data.BaseViewType
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.CommonViewType
import kotlinx.parcelize.Parcelize

@Parcelize
data class PendingLrItem(
    val amount: Int?,
    val billNo: String?,
    val imagePaths: List<ImagePath>,
    val qty: Int?,
    val saleBookID: String?,
    val salePartyEmail: String?,
    val salePartyMob: String?,
    val salePartyName: String?,
    val spplierInvNo: String?,
    val subPartyName: String?,
    val supplierMob: String?,
    val supplierName: String?
) : BaseWidget, Parcelable {
    override val viewType: BaseViewType get() = CommonViewType.DATA

    override fun getUniqueId(): String = billNo + saleBookID

    @Parcelize
    data class ImagePath(
        val url: String
    ) : Parcelable

}