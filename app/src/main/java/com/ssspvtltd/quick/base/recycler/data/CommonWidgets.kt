package com.ssspvtltd.quick.base.recycler.data

import com.ssspvtltd.quick.base.recycler.data.CommonViewType.BOTTOM_BORDER
import com.ssspvtltd.quick.base.recycler.data.CommonViewType.BOTTOM_PROGRESS_BAR
import com.ssspvtltd.quick.base.recycler.data.CommonViewType.PROGRESS_BAR

/**
 * Created by Abhishek Singh on May 03,2023.
 */

data class ProgressWidget(override val viewType: CommonViewType = PROGRESS_BAR) :
    BaseWidget {
    override fun getUniqueId(): String = PROGRESS_BAR.name + PROGRESS_BAR.id
}


data class BottomProgressWidget(override val viewType: CommonViewType = BOTTOM_PROGRESS_BAR) :
    BaseWidget {

    override fun getUniqueId(): String = BOTTOM_PROGRESS_BAR.name + BOTTOM_PROGRESS_BAR.id
}


data class BottomBorderWidget(override val viewType: CommonViewType = BOTTOM_BORDER) :
    BaseWidget {

    override fun getUniqueId(): String = BOTTOM_BORDER.name + BOTTOM_BORDER.id
}