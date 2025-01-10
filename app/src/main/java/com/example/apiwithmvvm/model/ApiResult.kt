package com.example.apiwithmvvm.model

sealed class ApiResult<T>(
    val data: T? = null,
    val message: String? = null,
    val statusCode: Int? = null
) {
    class Success<T>(data: T, statusCode: Int) : ApiResult<T>(data, statusCode = statusCode)
    class Error<T>(message: String?, statusCode: Int?) : ApiResult<T>(null, message, statusCode)
    class Loading<T> : ApiResult<T>()
}
