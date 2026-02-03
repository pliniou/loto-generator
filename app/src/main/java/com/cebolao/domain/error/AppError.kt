package com.cebolao.domain.error

import retrofit2.HttpException
import java.io.IOException

sealed interface AppError {
    data class Network(val code: Int? = null, val message: String? = null) : AppError

    data class DiskIO(val message: String? = null) : AppError

    data class DataCorruption(val message: String? = null) : AppError

    data class Validation(val message: String? = null) : AppError

    data class Unknown(val message: String? = null) : AppError
}

fun Throwable.toAppError(): AppError =
    when (this) {
        is HttpException -> AppError.Network(code = code(), message = message())
        is IOException -> AppError.Network(message = message)
        is IllegalArgumentException -> AppError.Validation(message = message)
        is IllegalStateException -> AppError.DataCorruption(message = message)
        else -> AppError.Unknown(message = message)
    }
