package com.ssspvtltd.quick_app.di

import com.ssspvtltd.quick_app.application.MainApplication
import com.ssspvtltd.quick_app.persistance.PrefHelper
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PrefHelperEntryPoint {
    fun getPrefHelper(): PrefHelper

    companion object {
        val prefHelper
            get() = EntryPointAccessors.fromApplication<PrefHelperEntryPoint>(
                MainApplication.instance.applicationContext
            ).getPrefHelper()
    }
}