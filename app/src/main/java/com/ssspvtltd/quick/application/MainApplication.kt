package com.ssspvtltd.quick.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.ssspvtltd.quick.utils.MediaUtils
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        localeContext = this
        MediaUtils.deleteTempFiles(this)
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: MainApplication

        @SuppressLint("StaticFieldLeak")
        lateinit var localeContext: Context
    }
}