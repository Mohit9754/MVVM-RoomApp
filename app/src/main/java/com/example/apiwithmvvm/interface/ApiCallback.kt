package com.example.apiwithmvvm.`interface`

import com.example.apiwithmvvm.model.ApiResult

interface ApiCallback<T> {

    fun onSuccess(result: ApiResult.Success<T>)

    fun onError(result: ApiResult.Error<T>)

}

