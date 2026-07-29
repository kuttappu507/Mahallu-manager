package com.mahallu.manager.core.result

/**
 * Generic result wrapper used across repositories and use-cases.
 */
sealed class DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : DataResult<Nothing>()
    data object Loading : DataResult<Nothing>()
}

@Suppress("UNCHECKED_CAST")
inline fun <T, R> DataResult<T>.map(transform: (T) -> R): DataResult<R> = when (this) {
    is DataResult.Success -> DataResult.Success(transform(data))
    is DataResult.Error -> this as DataResult<R>
    DataResult.Loading -> this as DataResult<R>
}