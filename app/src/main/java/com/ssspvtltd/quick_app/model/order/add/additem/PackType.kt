package com.ssspvtltd.quick_app.model.order.add.additem

import android.os.Parcelable
import com.ssspvtltd.quick_app.base.recycler.data.BaseItem
import com.ssspvtltd.quick_app.base.recycler.data.BaseViewType
import com.ssspvtltd.quick_app.base.recycler.data.BaseWidget
import com.ssspvtltd.quick_app.base.recycler.data.CommonViewType
import kotlinx.parcelize.Parcelize

@Parcelize
data class PackType(
    var pcsId: String,
    var packName: String?,
    var qty: String?,
    var amount: String?,
    var itemDetail: List<PackTypeItem>?
) : BaseWidget, BaseItem, Parcelable {
    override fun getUniqueId(): String = pcsId + packName
    override val viewType: BaseViewType get() = CommonViewType.DATA
}
