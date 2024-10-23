package com.ssspvtltd.quick_new.model.drawer

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ssspvtltd.quick_new.base.recycler.data.BaseWidget
import com.ssspvtltd.quick_new.model.DrawerViewType

data class DrawerItem(
    override val viewType: DrawerViewType,
    @StringRes val title: Int = 0,
    @StringRes val desc: Int? = null, 
    @DrawableRes val icon: Int? = null,
    // val action: DrawerAction? = null
) : BaseWidget {
    override fun getUniqueId(): String = toString() // Currently we don't have id
}