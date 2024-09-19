package com.ssspvtltd.quick.networking

sealed class ResultWrapper<out F, out S> {
    data class Success<out S>(val value: S) : ResultWrapper<Nothing, S>()
    data class Failure<out F>(val error: F) : ResultWrapper<F, Nothing>()

}