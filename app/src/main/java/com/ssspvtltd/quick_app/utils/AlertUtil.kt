package com.ssspvtltd.quick_app.utils

import android.content.Context
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.ssspvtltd.quick_app.R

/**
 * Created by Abhishek Singh on December 18,2021.
 */

enum class AlertType {
    NORMAL, ERROR, SUCCESS, WARNING
}

fun Context.showSuccessDialog(
    title: String = "Success",
    message: String = "Action performed successfully",
    confirmText: String = "OK",
    listener: (SweetAlertDialog) -> Unit = { it.dismissWithAnimation() }
) {
    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
        .setConfirmButtonTextColor(ContextCompat.getColor(this, R.color.success_text))
        .setConfirmButtonBackgroundColor(ContextCompat.getColor(this, R.color.success_bg))
        .setTitleText(title)
        .setContentText(message)
        .setConfirmText(confirmText)
        .setConfirmClickListener(listener)
        .show()
}

fun Context.showErrorDialog(
    title: String = "Oops",
    message: String = "Something went wrong. Please try again!",
    confirmText: String = "OK",
    listener: (SweetAlertDialog) -> Unit = { it.dismissWithAnimation() }
) {
    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
        .setConfirmButtonTextColor(ContextCompat.getColor(this, R.color.error_text))
        .setConfirmButtonBackgroundColor(ContextCompat.getColor(this, R.color.error_bg))
        .setTitleText(title)
        .setContentText(message)
        .setConfirmText(confirmText)
        .setConfirmClickListener(listener)
        .show()
}

fun Context.showWarningDialog(
    title: String = "Hold on!",
    message: String = "Please select correct action then proceed.",
    confirmText: String = "OK",
    cancelText: String = "Cancel",
    listener: (SweetAlertDialog) -> Unit = { it.dismissWithAnimation() }
) {
    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        .setConfirmButtonTextColor(ContextCompat.getColor(this, R.color.warning_text))
        .setConfirmButtonBackgroundColor(ContextCompat.getColor(this, R.color.warning_bg))
        .setTitleText(title)
        .setContentText(message)
        .setConfirmText(confirmText)
        .setCancelText(cancelText)
        .setConfirmClickListener(listener)
        .setCancelClickListener { it.dismissWithAnimation() }
        .show()
}

fun Context.showWarningDialog(
    title: String = "Hold on!",
    message: String = "Please select correct action then proceed.",
    confirmText: String = "OK",
    listener: (SweetAlertDialog) -> Unit = { it.dismissWithAnimation() }
) {
    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        .setConfirmButtonTextColor(ContextCompat.getColor(this, R.color.warning_text))
        .setConfirmButtonBackgroundColor(ContextCompat.getColor(this, R.color.warning_bg))
        .setTitleText(title)
        .setContentText(message)
        .setConfirmText(confirmText)
        .setConfirmClickListener(listener)
        .show()
}

