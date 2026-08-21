package com.ssspvtltd.quick.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
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

            val inputFormatter =
                DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                    .optionalStart()
                    .appendFraction(
                        ChronoField.NANO_OF_SECOND,
                        0,
                        9,
                        true
                    )
                    .optionalEnd()
                    .toFormatter(Locale.getDefault())

            val outputFormatter =
                DateTimeFormatter.ofPattern(
                    outputFormat.value,
                    Locale.getDefault()
                )

            val dateTime =
                LocalDateTime.parse(
                    date,
                    inputFormatter
                )

            dateTime.format(outputFormatter)

        } catch (e: Exception) {

            e.printStackTrace()
            ""
        }
    }


    fun format(
        dateStr: String?,
        outputFormat: String
    ): String {

        if (dateStr.isNullOrBlank()) return ""

        return try {

            val inputFormatter =
                DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                    .optionalStart()
                    .appendFraction(
                        ChronoField.NANO_OF_SECOND,
                        0,
                        9,
                        true
                    )
                    .optionalEnd()
                    .toFormatter(Locale.getDefault())

            val dateTime =
                LocalDateTime.parse(
                    dateStr,
                    inputFormatter
                )

            dateTime.format(
                DateTimeFormatter.ofPattern(
                    outputFormat,
                    Locale.getDefault()
                )
            )

        } catch (e: Exception) {

            e.printStackTrace()
            ""
        }
    }
}


enum class DateTimeFormat(val value: String) {

    DATE_TIME_FORMAT1(
        "yyyy-MM-dd'T'HH:mm:ss"
    ),

    DATE_TIME_FORMAT2(
        "dd MMM yyyy, hh:mm a"
    ),

    DATE_TIME_FORMAT3(
        "dd-MM-yyyy"
    ),

    DATE_TIME_FORMAT4(
        "MMM dd yyyy hh:mma"
    ),

    DATE_TIME_FORMAT5(
        "MM/dd/yyyy HH:mm:ss"
    ),

    DATE_TIME_FORMAT6(
        "dd/MM/yyyy hh:mm:ss a"
    )
}