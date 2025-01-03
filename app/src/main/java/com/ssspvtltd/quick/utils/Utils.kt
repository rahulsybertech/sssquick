package com.ssspvtltd.quick.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.ssspvtltd.quick.BuildConfig
import java.text.DecimalFormat

fun View.hideKeyBoard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}


fun amountFormat(amt: String): String {
    return try {
        val formatter = DecimalFormat("#,##,###.0")
        val number = amt.toDouble()
        formatter.format(number)
    } catch (e: NumberFormatException) {
        amt
    }
}

fun versionName(): String {
    return "Version ${BuildConfig.VERSION_NAME}"
}

