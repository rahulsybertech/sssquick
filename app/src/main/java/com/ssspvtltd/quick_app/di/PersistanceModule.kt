package com.ssspvtltd.quick_app.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ssspvtltd.quick_app.persistance.PrefHelper
import com.ssspvtltd.quick_app.persistance.PrefStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistanceModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().serializeNulls().create()
    }

    @Provides
    @Singleton
    fun providePrefHelper(prefStore: PrefStore): PrefHelper {
        return PrefHelper(prefStore)
    }

    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }
}