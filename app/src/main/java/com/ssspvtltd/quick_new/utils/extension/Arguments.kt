package com.ssspvtltd.quick_new.utils.extension

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable


@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Bundle.getParcelableExt(key: String): T? {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key) as? T
    } else {
        getParcelable(key, T::class.java)
    }
}

@Suppress("DEPRECATION")
inline fun <reified T : Serializable> Bundle.getSerializableExt(key: String): T? {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        getSerializable(key) as? T
    } else {
        getSerializable(key, T::class.java)
    }
}

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Intent.getParcelableExt(key: String): T? {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key) as? T
    } else {
        getParcelableExtra(key, T::class.java)
    }
}

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Intent.getParcelableArrayListExt(key: String): ArrayList<T>? {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        getParcelableArrayListExtra(key)
    } else {
        getParcelableArrayListExtra(key, T::class.java)
    }
}

@Suppress("DEPRECATION")
inline fun <reified T : Serializable> Intent.getSerializableExt(key: String): T? {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(key) as? T
    } else {
        getSerializableExtra(key, T::class.java)
    }
}

