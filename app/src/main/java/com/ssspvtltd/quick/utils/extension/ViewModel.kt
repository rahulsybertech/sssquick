package com.ssspvtltd.quick.utils.extension

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(): T {
    return ViewModelProvider(this)[T::class.java]
}

inline fun <reified T : ViewModel> Fragment.getViewModel(): T {
    return ViewModelProvider(this)[T::class.java]
}

inline fun <reified T : ViewModel> Fragment.getActivityViewModel(): T {
    return ViewModelProvider(requireActivity())[T::class.java]
}

inline fun <reified T : ViewModel> Fragment.getParentFragmentViewModel(): T {
    return ViewModelProvider(requireParentFragment())[T::class.java]
}
