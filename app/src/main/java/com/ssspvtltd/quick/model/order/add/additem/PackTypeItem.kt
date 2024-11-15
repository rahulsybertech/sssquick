package com.ssspvtltd.quick.model.order.add.additem

import android.os.Parcelable
import com.ssspvtltd.quick.base.recycler.data.BaseItem
import com.ssspvtltd.quick.base.recycler.data.BaseViewType
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.CommonViewType
import kotlinx.parcelize.Parcelize

@Parcelize
data class PackTypeItem(
    var itemID: String?,
    var itemName: String?,
    var itemQuantity: String?,
) : BaseWidget, BaseItem, Parcelable {
    override fun getUniqueId(): String = itemID + itemName
    override val viewType: BaseViewType get() = CommonViewType.DATA
}