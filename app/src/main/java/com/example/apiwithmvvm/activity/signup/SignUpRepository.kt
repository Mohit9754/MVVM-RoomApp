package com.example.apiwithmvvm.activity.signup

import com.example.apiwithmvvm.Retrofit.APIError
import com.example.apiwithmvvm.Retrofit.ApiService
import com.example.apiwithmvvm.Retrofit.RetrofitClient
import com.example.apiwithmvvm.database.UserRepository
import com.example.apiwithmvvm.`interface`.ApiCallback
import com.example.apiwithmvvm.model.ApiResponse
import com.example.apiwithmvvm.model.ApiResult
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class SignUpRepository @Inject constructor(private val apiService: ApiService)  {

    suspend fun signUp(
        profile: MultipartBody.Part,
        requestBodyMap: Map<String, RequestBody>,
        apiCallback: ApiCallback<ApiResponse>
    ) {

        try {

            val response = apiService.signUp(profile,requestBodyMap)

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


