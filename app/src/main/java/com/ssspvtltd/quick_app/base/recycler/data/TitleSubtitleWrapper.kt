package com.ssspvtltd.quick_app.base.recycler.data

/**
 * Created by Abhishek Singh on April 29,2023.
 */
data class TitleSubtitleWrapper(
    var id: String,
    var title: String? = null,
    var subtitle: String? = null,
    var actionText: String? = null,
    var anyObject: Any? = null,
) : BaseWidget {
    override val viewType: BaseViewType = CommonViewType.HEADER
    override fun getUniqueId(): String = id
}