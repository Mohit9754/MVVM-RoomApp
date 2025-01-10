package com.example.apiwithmvvm.fragment.more

import com.example.apiwithmvvm.Retrofit.APIError
import com.example.apiwithmvvm.Retrofit.ApiService
import com.example.apiwithmvvm.Retrofit.RetrofitClient
import com.example.apiwithmvvm.database.UserRepository
import com.example.apiwithmvvm.`interface`.ApiCallback
import com.example.apiwithmvvm.model.ApiResponse
import com.example.apiwithmvvm.model.ApiResult
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class MoreRepository @Inject constructor(private val userRepository: UserRepository, private val apiService: ApiService) {

    suspend fun deleteAccount(
        apiCallback: ApiCallback<ApiResponse>
    ) {
        val user = userRepository.getUser()

        try {

            val response = apiService.deleteUser(user.token)

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

    suspend fun deleteUser() {
        userRepository.deleteUser()
    }


}