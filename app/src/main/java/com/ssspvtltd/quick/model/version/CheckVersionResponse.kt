package com.ssspvtltd.quick.model.version


import com.google.gson.annotations.SerializedName

data class CheckVersionResponse(
    @SerializedName("data") val `data`: Data?,
    @SerializedName("error") val error: Boolean?, // false
    @SerializedName("message") val message: String?, // Record Found Successfully!!
    @SerializedName("responsecode") val responsecode: String?, // 200
    @SerializedName("success") val success: Boolean? // true
) {

    data class Data(
        @SerializedName("appName") val appName: String?, // SSSQUICK
        @SerializedName("companyId") val companyId: String?, // 43029624-ea4a-434c-9a14-d7da24840bad
        @SerializedName("id") val id: String?, // 50f9ec92-096f-48cf-a52d-adb2ba2dbad9
        @SerializedName("iosVersion") val iosVersion: Any?, // null
        @SerializedName("status") val status: Boolean?, // true
        @SerializedName("versionName") val versionName: String? // 45
    )
}