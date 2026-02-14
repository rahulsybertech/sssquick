package com.ssspvtltd.quick.di

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.gson.GsonBuilder
import com.ssspvtltd.quick.BuildConfig
import com.ssspvtltd.quick.constants.BASE_URL
import com.ssspvtltd.quick.networking.ApiService
import com.ssspvtltd.quick.networking.HeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideLoggingInceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
        }
    }
    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheSize = 10L * 1024 * 1024 // 10MB
        return Cache(File(context.cacheDir, "http_cache"), cacheSize)
    }
    @Provides
    @Singleton
    fun provideDynamicUrlInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()

            // Get URL from Remote Config (Fallback to constant BASE_URL)
            val remoteUrl = Firebase.remoteConfig.getString("api_base_url")
            val newBaseUrl = if (remoteUrl.isNotEmpty()) remoteUrl else BASE_URL

            val newHttpUrl = newBaseUrl.toHttpUrlOrNull()

            if (newHttpUrl != null) {
                val newUrl = request.url.newBuilder()
                    .scheme(newHttpUrl.scheme)
                    .host(newHttpUrl.host)
                    .port(newHttpUrl.port)
                    .build()
                request = request.newBuilder().url(newUrl).build()
            }

            chain.proceed(request)
        }
    }


    @Provides
    @Singleton
    fun provideOkHttpClient(
        headerInterceptor: HeaderInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,  dynamicUrlInterceptor: Interceptor,
        cache: Cache // <- Hilt injects it here
    ): OkHttpClient {
        val cacheInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            response.newBuilder()
                .header("Cache-Control", "public, max-age=60")
                .build()
        }

        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(headerInterceptor)
            .addInterceptor(dynamicUrlInterceptor) // Must be added after header but before logging
            .addInterceptor(loggingInterceptor)
            .connectTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}