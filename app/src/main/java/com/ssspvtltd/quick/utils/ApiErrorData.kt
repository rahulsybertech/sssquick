package com.ssspvtltd.quick.utils

import com.ssspvtltd.quick.networking.ApiResponse

data class ApiErrorData(var apiResponse: ApiResponse<*>, var titleMsg: String?)
