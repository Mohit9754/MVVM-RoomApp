package com.example.apiwithmvvm.activity.signup

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apitesting.basic.UtilityTools.Constants
import com.example.apitesting.basic.UtilityTools.Constants.Companion.profileImage
import com.example.apitesting.basic.UtilityTools.Utils
import com.example.apiwithmvvm.`interface`.ApiCallback
import com.example.apiwithmvvm.model.ApiResponse
import com.example.apiwithmvvm.model.ApiResult
import com.example.apiwithmvvm.validation.Validation
import com.example.apiwithmvvm.validation.ValidationModel
import com.example.apiwithmvvm.validation.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val repository: SignUpRepository) : ViewModel() {

    private val _validationResult = MutableLiveData<ValidationResult>()
    private val _signUpResult = MutableLiveData<ApiResult<ApiResponse>>()

    val validationResult: LiveData<ValidationResult> get() = _validationResult
    val signUpResult: LiveData<ApiResult<ApiResponse>> get() = _signUpResult

    @Inject
    lateinit var validation:Validation

    fun validate(
        profileBitmap: Bitmap,
        validationModelList: MutableList<ValidationModel>
    ) {

        val validationResult = validation.CheckValidation(validationModelList)

        if (validationResult.validationCheck) {

            signUp(
                validationModelList[1].data,
                validationModelList[2].data,
                validationModelList[3].data,
                validationModelList[4].data,
                profileBitmap
            )

        } else {

            _validationResult.value = validationResult

        }
    }

    private fun signUp(
        userName: String,
        name: String,
        email: String,
        password: String,
        profile: Bitmap
    ) {

        val hm = HashMap<String, String>().apply {
            put(Constants.userName, userName)
            put(Constants.name, name)
            put(Constants.email, email)
            put(Constants.password, password)
        }

        val requestBodyMap = HashMap<String, RequestBody>().apply {
            hm.forEach { (key, value) -> put(key, Utils.getRequestBody(value)) }
        }

        val profilePart = Utils.getImgMultipart(profileImage, profile)

        // Fetch result from repository and post value to LiveData
        viewModelScope.launch(Dispatchers.IO) {

            _signUpResult.postValue(ApiResult.Loading())

            // Observe the LiveData returned by the repository
            repository.signUp(profilePart, requestBodyMap, object : ApiCallback<ApiResponse> {

                override fun onSuccess(result: ApiResult.Success<ApiResponse>) {
                    _signUpResult.postValue(result)
                }

                override fun onError(result: ApiResult.Error<ApiResponse>) {
                    _signUpResult.postValue(result)
                }

            })
        }
    }
}
