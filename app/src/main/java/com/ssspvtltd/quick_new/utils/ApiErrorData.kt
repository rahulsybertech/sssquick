package com.ssspvtltd.quick_new.utils

import com.ssspvtltd.quick_new.networking.ApiResponse

data class ApiErrorData(var apiResponse: ApiResponse<*>, var titleMsg: String?)
