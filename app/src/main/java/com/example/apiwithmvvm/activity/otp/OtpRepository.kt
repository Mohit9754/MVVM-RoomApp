package com.example.apiwithmvvm.activity.otp

import com.example.apiwithmvvm.Retrofit.APIError
import com.example.apiwithmvvm.Retrofit.ApiService
import com.example.apiwithmvvm.`interface`.ApiCallback
import com.example.apiwithmvvm.model.ApiResponse
import com.example.apiwithmvvm.model.ApiResult
import com.example.apiwithmvvm.model.LoginResponse
import com.google.gson.Gson
import javax.inject.Inject

class OtpRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun verifyOTP(
        hashMap: HashMap<String, String>,
        apiCallback: ApiCallback<ApiResponse>
    ) {

        try {

            val response = apiService.verifyOtp(hashMap)

            if (response.isSuccessful) {
                response.body()?.let {
                    apiCallback.onSuccess(ApiResult.Success(it, response.code()))
                }
            } else {
                response.errorBody()?.let {
                    val message =
                        Gson().fromJson(response.errorBody()!!.charStream(), APIError::class.java)
                    apiCallback.onError(ApiResult.Error(message.message, response.code()))
                }
            }

        } catch (e: Exception) {
            apiCallback.onError(ApiResult.Error(e.message, null))
        }

    }

    suspend fun resendOTP(
        _id:String,
        apiCallback: ApiCallback<ApiResponse>
    ) {

        try {

            val response = apiService.resendOTP(_id)

            if (response.isSuccessful) {
                response.body()?.let {
                    apiCallback.onSuccess(ApiResult.Success(it, response.code()))
                }
            } else {
                response.errorBody()?.let {
                    val message =
                        Gson().fromJson(response.errorBody()!!.charStream(), APIError::class.java)
                    apiCallback.onError(ApiResult.Error(message.message, response.code()))
                }
            }

        } catch (e: Exception) {
            apiCallback.onError(ApiResult.Error(e.message, null))
        }

    }


}