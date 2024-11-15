package com.ssspvtltd.quick.base.recycler.data

interface BaseItem {
    fun getUniqueId(): String
    override fun equals(other: Any?): Boolean // Auto Implemented by data class
}