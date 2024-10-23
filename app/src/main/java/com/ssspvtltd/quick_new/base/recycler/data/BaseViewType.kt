package com.ssspvtltd.quick_new.base.recycler.data

/**
 * Created by Abhishek Singh on April 29,2023.
 */
interface BaseViewType {
    fun getID(): Int
    val id get() = getID()
}