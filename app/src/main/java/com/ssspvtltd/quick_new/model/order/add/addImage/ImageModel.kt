package com.ssspvtltd.quick_new.model.order.add.addImage

import android.os.Parcelable
import com.ssspvtltd.quick_new.base.recycler.data.BaseWidget
import com.ssspvtltd.quick_new.model.ImageViewType
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageModel(
    val filePath: String?,
    override val viewType: ImageViewType = ImageViewType.IMAGE,
) : BaseWidget, Parcelable {
    override fun getUniqueId(): String = filePath ?: toString()
}