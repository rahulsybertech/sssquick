package com.ssspvtltd.quick.model.auth

data class AutoLogout (
    var name: String,
    var loginType: String,
    var loginStatus: Boolean,
    var checkInStatus: Boolean,
    var temporaryLockStatus: Boolean,
    var tempLockReason: String,
)