package com.ssspvtltd.quick.utils

import java.text.SimpleDateFormat
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
    fun format(
        dateStr: String?,
        outputFormat: String
    ): String {
        if (dateStr.isNullOrBlank()) return ""

        val inputFormats = arrayOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss"
        )

        inputFormats.forEach { pattern ->
            try {
                val sdf = SimpleDateFormat(pattern, Locale.getDefault())
                val date = sdf.parse(dateStr) ?: return@forEach
                return SimpleDateFormat(outputFormat, Locale.getDefault()).format(date)
            } catch (_: Exception) { }
        }
        return ""
    }
}

enum class DateTimeFormat(val value: String) {
    DATE_TIME_FORMAT1("yyyy-MM-dd'T'HH:mm:ss"),
    DATE_TIME_FORMAT2("dd MMM yyyy, hh:mm a"),
    DATE_TIME_FORMAT3("dd-MM-yyyy"),

}