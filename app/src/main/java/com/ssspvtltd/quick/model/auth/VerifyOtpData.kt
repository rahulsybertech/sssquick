package com.ssspvtltd.quick.model.auth

data class VerifyOtpData(
    var accessToken: String,
    var companyId: String,
    var mobile: String,
    var name: String,
    var marketerCode: String,
    var accountId: String,
    var finYearId: String,
    var userId: String,
    var branchCompanyId: String,
    var appName: String,
)