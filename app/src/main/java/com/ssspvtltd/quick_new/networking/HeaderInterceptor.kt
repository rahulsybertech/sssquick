package com.ssspvtltd.quick_new.networking

import com.ssspvtltd.quick_new.di.PrefHelperEntryPoint
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader(
                "Authorization",
                  // runBlocking { PrefHelperEntryPoint.prefHelper.getAccessToken().orEmpty()})
                "Bearer " + runBlocking { PrefHelperEntryPoint.prefHelper.getAccessToken().orEmpty() })
            .build()
        return chain.proceed(newRequest)
    }
}