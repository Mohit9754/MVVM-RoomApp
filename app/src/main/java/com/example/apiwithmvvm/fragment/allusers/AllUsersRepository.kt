package com.example.apiwithmvvm.fragment.allusers

import com.example.apiwithmvvm.model.User
import com.example.apiwithmvvm.Retrofit.APIError
import com.example.apiwithmvvm.Retrofit.ApiService
import com.example.apiwithmvvm.Retrofit.RetrofitClient
import com.example.apiwithmvvm.database.UserRepository
import com.example.apiwithmvvm.`interface`.ApiCallback
import com.example.apiwithmvvm.model.ApiResult
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class AllUsersRepository @Inject constructor(private val userRepository: UserRepository, private val apiService: ApiService) {

    suspend fun getAllUsers(apiCallback: ApiCallback<List<User>>) {

        val token = userRepository.getUser().token

        try {

            val response = apiService.getAllUsers(token)

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


    suspend fun deleteAllUser() {
        userRepository.deleteUser()
    }


}