package com.ssspvtltd.quick_app.model.order.add

import java.io.Serializable

data class PackTypeData(
    val id: String?, val value: String?
) : Serializable {
    override fun toString(): String {
        return "$value"
    }
}
