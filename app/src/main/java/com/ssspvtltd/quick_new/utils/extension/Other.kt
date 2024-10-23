 package com.ssspvtltd.quick_new.utils.extension

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.util.TypedValue
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlin.math.roundToInt


/** returns integer dimensional value from the integer px value. */
internal val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(),
        Resources.getSystem().displayMetrics
    ).roundToInt()

/** returns float dimensional value from the float px value. */
internal val Float.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this,
        Resources.getSystem().displayMetrics
    )

fun Context.toast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    if (message == null) return
    Toast.makeText(this, message, duration).show()
}

fun Fragment.toast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().toast(message, duration)
}

fun <T> Fragment.setNavigationResult(key: String, value: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, value)
}

fun <T> Fragment.getNavigationResult(@IdRes id: Int, key: String, onResult: (result: T) -> Unit) {
    val navBackStackEntry = findNavController().getBackStackEntry(id)

    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME
            && navBackStackEntry.savedStateHandle.contains(key)
        ) {
            val result = navBackStackEntry.savedStateHandle.get<T>(key)
            result?.let(onResult)
            navBackStackEntry.savedStateHandle.remove<T>(key)
        }
    }
    navBackStackEntry.lifecycle.addObserver(observer)
    viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            navBackStackEntry.lifecycle.removeObserver(observer)
        }
    })

}

fun TextView.endDrawable(@DrawableRes id: Int = 0) {
    this.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, id, 0)
}

fun TextView.removeDrawables() {
    this.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
}

@ColorInt
fun Context.getThemeColor(@AttrRes attrRes: Int): Int {
    val materialColor = MaterialColors.getColor(this, attrRes, Color.TRANSPARENT)
    if (materialColor < 0) return materialColor

    val resolvedAttr = TypedValue()
    theme.resolveAttribute(attrRes, resolvedAttr, true)
    val colorRes = resolvedAttr.run { if (resourceId != 0) resourceId else data }
    return ContextCompat.getColor(this, colorRes)
}

fun Uri.isHttpUri(): Boolean {
    return this.scheme == "https" || this.scheme == "http"
}

fun <T> Flow<T>.observe(owner: LifecycleOwner, action: (value: T) -> Unit) {
    this.asLiveData(Dispatchers.Main).observe(owner, action)
}