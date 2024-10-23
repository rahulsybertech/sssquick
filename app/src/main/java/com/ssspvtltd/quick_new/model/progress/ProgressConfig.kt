package com.ssspvtltd.quick_new.model.progress

import androidx.annotation.ColorRes
import cn.pedant.SweetAlert.SweetAlertDialog
import com.ssspvtltd.quick_new.R

data class   ProgressConfig(
    val title: String? = "Please wait...",
    val message: String? = null,
    val type: Int = SweetAlertDialog.PROGRESS_TYPE,
    val cancelable: Boolean = false,
    @ColorRes val barColor: Int = R.color.color_A5DC86,
)
