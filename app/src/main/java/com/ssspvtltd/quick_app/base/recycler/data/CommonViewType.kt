package com.ssspvtltd.quick_app.base.recycler.data


/**
 * Created by Abhishek Singh on April 29,2023.
 */

/**
 * We are setting unique ordinal for each common enum so it will not conflicted
 * with particular defined enums
 */
enum class CommonViewType : BaseViewType {
    SHIMMER {
        override fun getID(): Int = -ordinal
    },
    HEADER {
        override fun getID(): Int = -ordinal
    },
    DATA {
        override fun getID(): Int = -ordinal
    },
    PROGRESS_BAR {
        override fun getID() = -ordinal
    },
    BOTTOM_PROGRESS_BAR {
        override fun getID() = -ordinal
    },
    BOTTOM_BORDER {
        override fun getID() = -ordinal
    },
}