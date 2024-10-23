package com.ssspvtltd.quick_new.model.order.goodsreturn

import android.os.Parcelable
import com.ssspvtltd.quick_new.base.recycler.data.BaseViewType
import com.ssspvtltd.quick_new.base.recycler.data.BaseWidget
import com.ssspvtltd.quick_new.base.recycler.data.CommonViewType
import kotlinx.parcelize.Parcelize

@Parcelize
data class GoodsReturnItem(
    val amount: Int?,
    val changedQty: Int?,
    val id: String,
    val itemName: String?,
    val noChangeQty: Int?,
    val qty: Int?,
    val remark: String?,
    val saleBillNo: String?,
    val salePartyEmail: String?,
    val salePartyMob: String?,
    val salePartyName: String?,
    val status: String?,
    val subPartyName: String?,
    val supplierMob: String?,
    val supplierName: String?
) : BaseWidget, Parcelable {
    override val viewType: BaseViewType get() = CommonViewType.DATA
    override fun getUniqueId(): String = id + saleBillNo
}
