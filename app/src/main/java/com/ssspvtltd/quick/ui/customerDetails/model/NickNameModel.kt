package com.ssspvtltd.quick.ui.customerDetails.model

data class NickNameModel(
    val id: String?,
    val nickName: String?
) {
    override fun toString(): String {
        return nickName ?: ""
    }
}