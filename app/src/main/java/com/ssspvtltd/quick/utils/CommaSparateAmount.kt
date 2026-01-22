package com.ssspvtltd.quick.utils

import androidx.compose.ui.text.intl.Locale
import java.text.NumberFormat

object CommaSparateAmount {

    fun formatIndianAmount(amount: Double?): String {
        if (amount == null) return "0.00"

        return try {
            val formatter = NumberFormat.getNumberInstance(java.util.Locale("en", "IN"))
            formatter.minimumFractionDigits = 2
            formatter.maximumFractionDigits = 2
            formatter.format(amount)
        } catch (e: Exception) {
            "0.00"
        }
    }

    fun formatIndianAmount(amount: String?): String {
        if (amount.isNullOrBlank()) return "0.00"

        return try {
            val cleanAmount = amount.replace(",", "")
            formatIndianAmount(cleanAmount.toDouble())
        } catch (e: Exception) {
            "0.00"
        }
    }
}
