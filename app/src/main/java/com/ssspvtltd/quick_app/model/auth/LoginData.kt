package com.ssspvtltd.quick_app.model.auth

data class LoginData(
    var loginStatus: Boolean,
    var loginType: String,
    var name: String,
    var otp: String,
    var tempLockReason: String,
    var temporaryLockStatus: Boolean
)