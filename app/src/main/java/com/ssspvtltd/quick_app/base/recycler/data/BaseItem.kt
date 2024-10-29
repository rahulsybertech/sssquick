package com.ssspvtltd.quick_app.base.recycler.data

interface BaseItem {
    fun getUniqueId(): String
    override fun equals(other: Any?): Boolean // Auto Implemented by data class
}