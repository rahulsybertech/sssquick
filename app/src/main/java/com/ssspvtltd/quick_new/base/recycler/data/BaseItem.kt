package com.ssspvtltd.quick_new.base.recycler.data

interface BaseItem {
    fun getUniqueId(): String
    override fun equals(other: Any?): Boolean // Auto Implemented by data class
}