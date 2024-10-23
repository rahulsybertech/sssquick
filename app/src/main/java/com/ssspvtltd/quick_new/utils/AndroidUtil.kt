package com.ssspvtltd.quick_new.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.provider.Settings
import android.webkit.MimeTypeMap
import com.ssspvtltd.quick_new.application.MainApplication
import com.ssspvtltd.quick_new.persistance.PrefHelper
import com.ssspvtltd.quick_new.utils.extension.isNotNullOrBlank
import tech.developingdeveloper.toaster.Toaster
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.regex.Pattern
import javax.annotation.Nullable


fun getOvalShape(
    backgroundColor: Int,
    borderColor: Int = 0,
    drawBorder: Boolean = false,
    borderWidth: Int = 3
): GradientDrawable {
    val shape = GradientDrawable()
    shape.shape = GradientDrawable.OVAL
    shape.setColor(backgroundColor)

    if (drawBorder) {
        shape.setStroke(borderWidth, borderColor)
    }
    return shape
}

fun getRectShape(
    backgroundColor: Int,
    borderColor: Int,
    radius: Float,
    drawBorder: Boolean = true,
    borderWidth: Int = 3,
    onlyLeftRadius: Boolean = false,
): GradientDrawable {
    val shape = GradientDrawable()
    shape.shape = GradientDrawable.RECTANGLE
    if (onlyLeftRadius) {
        // shape.cornerRadii = floatArrayOf(radius, radius, 0f, 0f, 0f, 0f, 0f, 0f)
    } else {
        // shape.cornerRadii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
    }
    shape.setColor(backgroundColor)
    if (drawBorder) {
        shape.setStroke(borderWidth, borderColor)
    }

    return shape
}

fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

fun prepareAppWebViewUrl(url: String): String {
    return if (url.contains("?")) "$url&noheader=true" else "$url?noheader=true"
}

fun formatPrice(priceHtml: String?): String {
    return if (priceHtml.isNullOrBlank()) ""
    else priceHtml.replace("[\\t]+".toRegex(), "").replace("del", "s")
}

suspend fun isUserLogin(prefHelper: PrefHelper): Boolean {
    val userId = prefHelper.getUserName()
    return userId.isNotNullOrBlank()
}

fun CharSequence.isPhoneNumber(): Boolean {
    val REG = "^[6-9]\\d{9}\$"
    var PATTERN: Pattern = Pattern.compile(REG)
    return PATTERN.matcher(this).find()
}

@SuppressLint("HardwareIds")
fun getDeviceUniqId(): String {
    return Settings.Secure.getString(
        MainApplication.localeContext.contentResolver,
        Settings.Secure.ANDROID_ID
    )
}


// fun formatAddress(info: AddressInfo): String {
//    val sb = StringBuilder("")
//    if (info.name.isNotBlank()) sb.append(info.name + "\n")
//
//    if (info.address_1.isNotBlank() && info.address_2.isNotBlank()) {
//        "${info.address_1}, ${info.address_2}"
//    } else if (info.address_1.isNotBlank()) {
//        sb.append(info.address_1 + "\n")
//    } else if (info.address_2.isNotBlank()) {
//        sb.append(info.address_2 + "\n")
//    }
//
//    if (info.city.isNotBlank() && info.state.isNotBlank()) {
//        sb.append("${info.city}, ${info.state}\n")
//    } else if (info.city.isNotBlank()) {
//        sb.append("${info.city}\n")
//    } else if (info.state.isNotBlank()) {
//        sb.append("${info.state}\n")
//    }
//
//    if (info.country.isNotBlank() && info.postcode.isNotBlank()) {
//        sb.append("${info.country} - ${info.postcode}\n")
//    } else if (info.country.isNotBlank()) {
//        sb.append(info.country + "\n")
//    } else if (info.postcode.isNotBlank()) {
//        sb.append(info.postcode + "\n")
//    }
//
//    if (!info.email.isNullOrBlank() && !info.phone.isNullOrBlank()) {
//        sb.append("Email: ${info.email}\nPhone: ${info.phone}\n")
//    } else if (!info.email.isNullOrBlank()) {
//        sb.append("Email: ${info.email}\n")
//    } else if (!info.phone.isNullOrBlank()) {
//        sb.append("Phone: ${info.phone}\n")
//    }
//
//    return sb.toString().trim()
//}

@Nullable
fun createCopyAndReturnRealPath(context: Context, uri: Uri): File? {
    val contentResolver = context.contentResolver ?: return null

    // Create file path inside app's cache dir
    val filePath = context.cacheDir.absolutePath + File.separator +
            System.currentTimeMillis() + "." + getMimeType(context, uri)

    val file = File(filePath)
    file.deleteOnExit()
    try {
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val outputStream: OutputStream = FileOutputStream(file)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()
    } catch (ignore: IOException) {
        return null
    }
    return file
}

fun getMimeType(context: Context, uri: Uri): String {
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))
        ?: "jpg"
}

fun showToast(message: String?, duration: Int = Toaster.LENGTH_SHORT, context: Context? = null) {
    if (message.isNullOrBlank()) return
    Toaster.pop(context ?: MainApplication.localeContext, message, duration).show()
}

