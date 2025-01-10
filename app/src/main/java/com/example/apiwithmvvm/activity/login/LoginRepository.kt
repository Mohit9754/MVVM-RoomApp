package com.example.apiwithmvvm.activity.login

import com.example.apiwithmvvm.Retrofit.APIError
import com.example.apiwithmvvm.Retrofit.ApiService
import com.example.apiwithmvvm.Retrofit.RetrofitClient
import com.example.apiwithmvvm.database.User
import com.example.apiwithmvvm.database.UserRepository
import com.example.apiwithmvvm.`interface`.ApiCallback
import com.example.apiwithmvvm.model.ApiResult
import com.example.apiwithmvvm.model.LoginResponse
import com.google.gson.Gson
import javax.inject.Inject


class LoginRepository @Inject constructor(private val userRepository: UserRepository,private val apiService: ApiService) {

    suspend fun login(
        hashMap: HashMap<String, String>,
        apiCallback: ApiCallback<LoginResponse>
    ) {

        try {

            val response = apiService.login(hashMap)

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

    suspend fun insertUser(user: User) {
        userRepository.insertUser(user)
    }
}