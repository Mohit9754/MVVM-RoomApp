package com.example.apiwithmvvm.fragment.profile

import com.example.apitesting.model.UpdateResponse
import com.example.apiwithmvvm.Retrofit.APIError
import com.example.apiwithmvvm.Retrofit.ApiService
import com.example.apiwithmvvm.Retrofit.RetrofitClient
import com.example.apiwithmvvm.database.User
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

class ProfileRepository @Inject constructor(private val userRepository: UserRepository, private val apiService: ApiService) {

    suspend fun getUser(): User {
        return userRepository.getUser()
    }

    suspend fun updateUser(user: User): Int {
        return userRepository.updateUser(user)
    }

    suspend fun deleteAllUser() {
        userRepository.deleteUser()
    }

    suspend fun update(
        token:String,
        profile: MultipartBody.Part,
        name:RequestBody,
        email:RequestBody,
        apiCallback: ApiCallback<UpdateResponse>
    ) {

        try {

            val response = apiService.updateUser(token,profile,name, email)

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