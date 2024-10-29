package com.ssspvtltd.quick_app.networking

import androidx.annotation.StringRes
import com.google.gson.Gson
import com.ssspvtltd.quick_app.R
import com.ssspvtltd.quick_app.application.MainApplication
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiRequestCode: Int, apiCall: suspend () -> T
): ResultWrapper<ApiResponse<*>, T> {
    return withContext(dispatcher) {
        try {
            val response = apiCall.invoke()
            if (response is ApiResponse<*>) {
                response.apiRequestCode = apiRequestCode
                if (response.success == true) ResultWrapper.Success(response)
                else ResultWrapper.Failure(response)
            } else if (response is Response<*>) {
                if (response.isSuccessful) ResultWrapper.Success(response)
                else ResultWrapper.Failure(
                    getErrorObject(
                        apiRequestCode, NetworkStatus.NETWORK_FAILURE.message,
                        response.code().toString()
                    )
                )
            } else {
                ResultWrapper.Failure(
                    getErrorObject(apiRequestCode, NetworkStatus.NETWORK_FAILURE.message)
                )
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is SocketException,
                is TimeoutException,
                is UnknownHostException,
                is SocketTimeoutException -> ResultWrapper.Failure(
                    getErrorObject(apiRequestCode, NetworkStatus.CONNECTION_ERROR.message)
                )

                is IOException -> ResultWrapper.Failure(
                    getErrorObject(apiRequestCode, NetworkStatus.NETWORK_FAILURE.message)
                )

                is HttpException -> ResultWrapper.Failure(
                    convertErrorBody(apiRequestCode, throwable)
                )

                else -> ResultWrapper.Failure(
                    getErrorObject(apiRequestCode, throwable.localizedMessage)
                )
            }
        }
    }
}

private fun convertErrorBody(apiRequestCode: Int, httpException: HttpException): ApiResponse<*> {
    return try {
        Gson().fromJson(httpException.response()?.errorBody()?.string(), ApiResponse::class.java)
            .apply {
                this.apiRequestCode = apiRequestCode
            }
    } catch (throwable: Throwable) {
        getErrorObject(apiRequestCode, throwable.localizedMessage, httpException.code().toString())
    }
}

private fun getErrorObject(
    apiRequestCode: Int,
    message: String? = null,
    responseCode: String? = null
): ApiResponse<*> {
    return ApiResponse<Any>(
        apiRequestCode = apiRequestCode,
        success = false, error = true,
        responseCode = responseCode,
        message = message ?: NetworkStatus.NETWORK_FAILURE.message,
    )
}

enum class NetworkStatus(@StringRes private val resource: Int) {
    NETWORK_FAILURE(R.string.something_went_wrong),
    CONNECTION_ERROR(R.string.connection_timeout);

    val message: String
        get() = MainApplication.localeContext.getString(resource)
}
