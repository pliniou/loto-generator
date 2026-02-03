package com.cebolao.domain.result

import com.cebolao.domain.error.AppError
import com.cebolao.domain.error.toAppError

sealed interface AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>

    data class Failure(val error: AppError, val cause: Throwable? = null) : AppResult<Nothing>
}

suspend inline fun <T> appResultSuspend(crossinline block: suspend () -> T): AppResult<T> =
    try {
        AppResult.Success(block())
    } catch (t: Throwable) {
        AppResult.Failure(error = t.toAppError(), cause = t)
    }
