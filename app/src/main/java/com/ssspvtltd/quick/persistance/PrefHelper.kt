package com.ssspvtltd.quick.persistance

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class PrefHelper(private val prefStore: PrefStore) {

    suspend fun getAccessToken(): String? {
        return prefStore.getValue(PrefKeys.KEY_ACCESS_TOKEN).firstOrNull()
    }

    fun getAccessTokenAsFlow(): Flow<String?> {
        return prefStore.getValue(PrefKeys.KEY_ACCESS_TOKEN)
    }

    suspend fun setAccessToken(token: String?) {
        prefStore.setValue(PrefKeys.KEY_ACCESS_TOKEN, token.orEmpty())
    }

    suspend fun setUserName(userName: String?) {
        prefStore.setValue(PrefKeys.KEY_USER_NAME, userName.orEmpty())
    }

    suspend fun setOrderId(orderId: String?) {
        prefStore.setValue(PrefKeys.KEY_USER_NAME, orderId.orEmpty())
    }

    suspend fun getOrderId(): String? {
        return prefStore.getValue(PrefKeys.KEY_USER_NAME).firstOrNull()
    }

    suspend fun getMarketerCode(): String? {
        return prefStore.getValue(PrefKeys.KEY_MARKETER_CODE).firstOrNull()
    }

    suspend fun setMarketerCode(marketerCode: String?) {
        prefStore.setValue(PrefKeys.KEY_MARKETER_CODE, marketerCode.orEmpty())
    }

    suspend fun getAccountId(): String? {
        return prefStore.getValue(PrefKeys.KEY_ACCOUNT_ID).firstOrNull()
    }

    suspend fun setAccountId(accountId: String?) {
        prefStore.setValue(PrefKeys.KEY_ACCOUNT_ID, accountId.orEmpty())
    }

    suspend fun getMarketerMobile(): String? {
        return prefStore.getValue(PrefKeys.KEY_MARKETER_MOBILE).firstOrNull()
    }

    suspend fun setMarketerMobile(marketerMobile: String?) {
        prefStore.setValue(PrefKeys.KEY_MARKETER_MOBILE, marketerMobile.orEmpty())
    }

    suspend fun getUserName(): String? {
        return prefStore.getValue(PrefKeys.KEY_USER_NAME).firstOrNull()
    }


    suspend fun setCheckinStatus(status: Boolean) {
        prefStore.setValue(PrefKeys.KEY_CHECKIN_STATUS, status ?: false)
    }

    suspend fun getCheckinStatus(): Boolean? {
        return prefStore.getValue(PrefKeys.KEY_CHECKIN_STATUS).firstOrNull()
    }

    fun getUserNameAsFlow(): Flow<String?> {
        return prefStore.getValue(PrefKeys.KEY_USER_NAME)
    }

    fun getUserNameAsFlow1(): Flow<String?> {
        return prefStore.getValue(PrefKeys.KEY_USER_NAME)
    }


    suspend fun clearPref() {
        prefStore.clear()
    }

    suspend fun logout() {
        prefStore.clear()
    }

}