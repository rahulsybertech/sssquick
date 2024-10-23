package com.ssspvtltd.quick_new.base.recycler.data


/**
 * Created by Abhishek Singh on April 29,2023.
 */
interface BaseWidget {
    val viewType: BaseViewType?
    fun getUniqueId(): String
    override fun equals(other: Any?): Boolean // Auto Implemented by data class
}

