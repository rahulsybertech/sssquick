package com.ssspvtltd.quick.networking

import com.ssspvtltd.quick.di.PrefHelperEntryPoint
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader(
                "Authorization",
                  // runBlocking { PrefHelperEntryPoint.prefHelper.getAccessToken().orEmpty()})
                "Bearer " + runBlocking {
                   PrefHelperEntryPoint.prefHelper.getAccessToken().orEmpty()
            //       "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoiU1VCSEFTIFNJTkdIIiwiQ29tcGFueUlkIjoiNDMwMjk2MjQtZWE0YS00MzRjLTlhMTQtZDdkYTI0ODQwYmFkIiwiQWNjb3VudElkIjoiNDI2MmFmNTctYzA3Mi00YjMxLTliNGQtM2YzNjg4NzgwMTViIiwiTW9iaWxlTm8iOiI2Mjg5OTAyMDY3IiwiRmluWWVhcklkIjoiOTRhYmM4Y2ItYzAyOS00NmM3LWExMTQtNmQ5MGFkZDExNzBhIiwiTWFya2V0ZXJDb2RlIjoiU1NTIiwiVXNlcklkIjoiNzViOTM5NDktZmYzZC00M2I5LThhYmYtM2ViZTA1OTU3MzI4IiwiQnJhbmNoQ29tcGFueUlkIjoiNmYxZDI4NWYtZmU2ZC00MzJlLWIzNTItYWFkNTRmOTY1NWIyIiwiQXBwTmFtZSI6IlF1aWNrIiwiaHR0cDovL3NjaGVtYXMubWljcm9zb2Z0LmNvbS93cy8yMDA4LzA2L2lkZW50aXR5L2NsYWltcy9leHBpcmF0aW9uIjoiTm92IFR1ZSAxMSAyMDI1IDA5OjM0OjM2IEFNIiwibmJmIjoxNzQ3MzAxNjc2LCJleHAiOjE3NjI4MzM4NzYsImlzcyI6Imh0dHBzOi8vbG9jYWxob3N0OjQyMDAiLCJhdWQiOiJodHRwczovL2xvY2FsaG9zdDo1MDAwIn0.tUEZLvdJSoBIJ14bvLtdZ2x6Xa2Aln30OofUKUXANU0"
                })
            .build()
        return chain.proceed(newRequest)
    }
}