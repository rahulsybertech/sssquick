package com.ssspvtltd.quick_new.persistance

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PrefStore.STORE_NAME)

@Singleton
class PrefStore @Inject constructor(
    private val gson: Gson,
    @ApplicationContext private val context: Context
) {

    suspend fun <T> setObject(prefKey: Preferences.Key<String>, value: T?, clazz: Class<T>) {
        val str = gson.toJson(value, clazz)
        context.dataStore.edit {
            it[prefKey] = str
        }
    }

    fun <T> getObject(prefKey: Preferences.Key<String>, clazz: Class<T>): Flow<T?> {
        return context.dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { gson.fromJson(it[prefKey], clazz) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun <T> setValue(prefKey: Preferences.Key<T>, value: T) {
        context.dataStore.edit {
            it[prefKey] = value
        }
    }

    fun <T> getValue(prefKey: Preferences.Key<T>): Flow<T?> {
        return context.dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { it[prefKey] }
            .flowOn(Dispatchers.IO)
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun <T> clear(prefKey: Preferences.Key<T>) {
        context.dataStore.edit { it.remove(prefKey) }
    }

    companion object {
        const val STORE_NAME = "sssquick_data_store"
    }
}

object PrefKeys {
    val KEY_ACCESS_TOKEN = stringPreferencesKey("KEY_ACCESS_TOKEN")
    val KEY_USER_NAME = stringPreferencesKey("USER_NAME")
    val KEY_IS_USER_LOGEDIN = booleanPreferencesKey("IS_USER_LOGEDIN")
    val KEY_CHECKIN_STATUS = booleanPreferencesKey("CHECKIN_STATUS")
    val KEY_IS_CHECKEDIN = booleanPreferencesKey("IS_CHECKEDIN")
    val KEY_MARKETER_CODE = stringPreferencesKey("MARKETER_CODE")
    val KEY_ACCOUNT_ID = stringPreferencesKey("ACCOUNT_ID")
    val KEY_MARKETER_MOBILE = stringPreferencesKey("MARKETER_MOBILE")
}