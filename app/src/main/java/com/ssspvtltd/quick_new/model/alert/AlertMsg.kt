package com.ssspvtltd.quick_new.model.alert

import androidx.annotation.ColorRes
import cn.pedant.SweetAlert.SweetAlertDialog
import com.ssspvtltd.quick_new.R

data class AlertMsg(
    val title: String? = null,
    val message: String? = null,
    val type: Int = SweetAlertDialog.ERROR_TYPE,
    val cancelable: Boolean = false,
    val okButtonText: String? = "OK",
    @ColorRes val barColor: Int = R.color.color_A5DC86,
    @ColorRes var btnBgColor : Int = R.color.error_text
)


