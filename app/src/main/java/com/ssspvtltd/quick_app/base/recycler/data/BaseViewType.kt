package com.ssspvtltd.quick_app.base.recycler.data

/**
 * Created by Abhishek Singh on April 29,2023.
 */
interface BaseViewType {
    fun getID(): Int
    val id get() = getID()
}