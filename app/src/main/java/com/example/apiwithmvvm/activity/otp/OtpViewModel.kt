package com.example.apiwithmvvm.activity.otp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apitesting.basic.UtilityTools.Constants
import com.example.apiwithmvvm.`interface`.ApiCallback
import com.example.apiwithmvvm.model.ApiResponse
import com.example.apiwithmvvm.model.ApiResult
import com.example.apiwithmvvm.model.LoginResponse
import com.example.apiwithmvvm.validation.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel  @Inject constructor(private val repository: OtpRepository) : ViewModel() {

    private val _verifyOTPResult = MutableLiveData<ApiResult<ApiResponse>>()
    val verifyOTPResult: LiveData<ApiResult<ApiResponse>> get() = _verifyOTPResult

    private val _resendOTPResult = MutableLiveData<ApiResult<ApiResponse>>()
    val resendOTPResult: LiveData<ApiResult<ApiResponse>> get() = _resendOTPResult

    fun verifyOTP(_id:String,otp:String) {

        val hm = HashMap<String, String>()
        hm[Constants._id] = _id
        hm[Constants.otp] = otp

        viewModelScope.launch(Dispatchers.IO) {

            _verifyOTPResult.postValue(ApiResult.Loading())

            // Observe the LiveData returned by the repository
            repository.verifyOTP(hm, object : ApiCallback<ApiResponse> {

                override fun onSuccess(result: ApiResult.Success<ApiResponse>) {
                    _verifyOTPResult.postValue(result)
                }

                override fun onError(result: ApiResult.Error<ApiResponse>) {
                    _verifyOTPResult.postValue(result)
                }

            })
        }



    }

    fun resendOTP(_id:String) {

        _resendOTPResult.postValue(ApiResult.Loading())

        viewModelScope.launch(Dispatchers.IO) {

            repository.resendOTP(_id, object : ApiCallback<ApiResponse> {

                override fun onSuccess(result: ApiResult.Success<ApiResponse>) {
                    _resendOTPResult.postValue(result)
                }

                override fun onError(result: ApiResult.Error<ApiResponse>) {
                    _resendOTPResult.postValue(result)
                }

            })
        }



    }


}