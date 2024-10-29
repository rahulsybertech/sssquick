package com.ssspvtltd.quick_app.utils

import com.ssspvtltd.quick_app.networking.ApiResponse

data class ApiErrorData(var apiResponse: ApiResponse<*>, var titleMsg: String?)
