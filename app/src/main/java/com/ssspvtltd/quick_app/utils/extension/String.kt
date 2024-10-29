package com.ssspvtltd.quick_app.utils.extension

import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Created by Abhishek Singh on February 04,2024.
 */
 
fun String?.orElse(other: String): String {
    return if (this.isNullOrBlank()) other else this
}

fun String?.isNotNullOrBlank(): Boolean {
    return !this.isNullOrBlank()
}

fun String?.toUri(): Uri? {
    return if (this.isNullOrBlank()) null else Uri.parse(this)
}

fun String?.createRequestBody(): RequestBody? {
    return this?.toRequestBody("text/plain;charset=utf-8".toMediaTypeOrNull())
}
