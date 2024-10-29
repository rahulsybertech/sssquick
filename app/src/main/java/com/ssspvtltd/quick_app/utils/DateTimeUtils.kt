package com.ssspvtltd.quick_app.utils

import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {
    @JvmStatic
    fun formatDate(
        date: String?,
        inputFormat: DateTimeFormat,
        outputFormat: DateTimeFormat
    ): String {
        if (date.isNullOrBlank()) return ""
        return try {
            val inputFormatter = DateTimeFormatter.ofPattern(inputFormat.value, Locale.getDefault())
            val outputFormatter =
                DateTimeFormatter.ofPattern(outputFormat.value, Locale.getDefault())
            outputFormatter.format(inputFormatter.parse(date))
        } catch (_: Throwable) {
            ""
        }
    }
}

enum class DateTimeFormat(val value: String) {
    DATE_TIME_FORMAT1("yyyy-MM-dd'T'HH:mm:ss"),
    DATE_TIME_FORMAT2("dd-MM-yyyy"),
    DATE_TIME_FORMAT3("dd-MM-yyyy"),
}