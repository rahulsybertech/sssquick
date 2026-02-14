package com.ssspvtltd.quick.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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
fun amountFormatNew(amount: Double): String {
    val formatter = DecimalFormat("#,##,###.##")
    return formatter.format(amount)
}

fun versionName(): String {
    return "Version ${BuildConfig.VERSION_NAME}"
}
fun TextView.setLabeledText(
    label: String,
    labelColorRes: Int,
    value: String
) {
    val labelColor = ContextCompat.getColor(context, labelColorRes)
    val ssb = SpannableStringBuilder()
        // append the label and color it
        .append(label).apply {
            setSpan(
                ForegroundColorSpan(labelColor),
                0, label.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        // append the value in default text color
        .append(value)
    text = ssb
}




