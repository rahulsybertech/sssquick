package com.ssspvtltd.quick.utils

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

object RemoteConfigManager {
    private const val TAG = "RemoteConfigManager"
    private const val BASE_URL_KEY = "base_url"

    fun initConfig(onComplete: (String) -> Unit) {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // Fetch once per hour
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Set default value in case of no internet
        remoteConfig.setDefaultsAsync(mapOf(BASE_URL_KEY to "https://appapi.ssspltd.com/"))

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            val url = if (task.isSuccessful) {
                remoteConfig.getString(BASE_URL_KEY)
            } else {
                remoteConfig.getString(BASE_URL_KEY) // Fallback to default
            }
            Log.d(TAG, "Fetched URL: $url")
            onComplete(url)
        }
    }
}