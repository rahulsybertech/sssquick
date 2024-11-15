package com.ssspvtltd.quick.model.order.add.additem

import android.os.Parcelable
import com.ssspvtltd.quick.base.recycler.data.BaseItem
import com.ssspvtltd.quick.base.recycler.data.BaseViewType
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.CommonViewType
import kotlinx.parcelize.Parcelize

@Parcelize
data class PackType(
    var id : String?,
    var pcsId: String,
    var packName: String?,
    var qty: String?,
    var amount: String?,
    var itemDetail: List<PackTypeItem>?
) : BaseWidget, BaseItem, Parcelable {
    override fun getUniqueId(): String = pcsId + packName
    override val viewType: BaseViewType get() = CommonViewType.DATA
}
