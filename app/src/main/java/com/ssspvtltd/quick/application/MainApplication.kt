package com.ssspvtltd.quick.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.google.firebase.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.ssspvtltd.quick.utils.MediaUtils
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
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