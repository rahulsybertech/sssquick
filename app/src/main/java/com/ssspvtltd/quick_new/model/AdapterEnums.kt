package com.ssspvtltd.quick_new.model

import com.ssspvtltd.quick_new.base.recycler.data.BaseViewType


enum class CheckInViewType : BaseViewType {
    CHECK_IN_SHIMMER, CHECK_IN_DATA, ;

    override fun getID(): Int = ordinal
}

enum class DrawerViewType : BaseViewType {
    VIEW_TYPE_DRAWER_ITEM, VIEW_TYPE_DRAWER_HEADER, VIEW_TYPE_SMALL_DIVIDER, VIEW_TYPE_LARGE_DIVIDER, ;

    override fun getID(): Int = ordinal
}

enum class ImageViewType : BaseViewType {
    ADD_IMAGE,
    IMAGE;

    override fun getID(): Int = ordinal
}