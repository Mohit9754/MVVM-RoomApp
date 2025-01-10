package com.example.apiwithmvvm.fragment.profile

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apitesting.basic.UtilityTools.Constants.Companion.profileImage
import com.example.apitesting.basic.UtilityTools.Utils
import com.example.apitesting.model.UpdateResponse
import com.example.apiwithmvvm.database.User
import com.example.apiwithmvvm.`interface`.ApiCallback
import com.example.apiwithmvvm.model.ApiResult
import com.example.apiwithmvvm.validation.Validation
import com.example.apiwithmvvm.validation.ValidationModel
import com.example.apiwithmvvm.validation.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository) : ViewModel() {

    private val _user = MutableLiveData<User>()
    private val _updateResult = MutableLiveData<ApiResult<UpdateResponse>>()
    private val _validationResult = MutableLiveData<ValidationResult>()

    val user: LiveData<User> get() = _user
    val updateResult: LiveData<ApiResult<UpdateResponse>> get() = _updateResult
    val validationResult: LiveData<ValidationResult> get() = _validationResult

    @Inject
    lateinit var validation:Validation

    init {
        // Automatically fetch data when the ViewModel is created
        viewModelScope.launch(Dispatchers.IO) {
            getUser()
        }
    }

    private suspend fun getUser() {
        _user.postValue(repository.getUser())
    }

    fun validate(profileBitmap: Bitmap,validationModelList: List<ValidationModel>) {

        val validationResult = validation.CheckValidation(validationModelList)

        if (validationResult.validationCheck) {

            updateUser(profileBitmap, validationModelList[0].data, validationModelList[1].data)

        }
        else {

            _validationResult.value = validationResult

        }

    }

    private fun updateUser(profileBitmap: Bitmap, name: String, email: String) {

        val token = user.value?.token ?: return

        val rbName = Utils.getRequestBody(name)
        val rbEmail = Utils.getRequestBody(email)

        val profilePart = Utils.getImgMultipart(profileImage, profileBitmap)

        // Fetch result from repository and post value to LiveData
        viewModelScope.launch(Dispatchers.IO) {

            _updateResult.postValue(ApiResult.Loading())

            // Observe the LiveData returned by the repository
            repository.update(
                token,
                profilePart,
                rbName,
                rbEmail,
                object : ApiCallback<UpdateResponse> {

                    override fun onSuccess(result: ApiResult.Success<UpdateResponse>) {
                        _updateResult.postValue(result)
                    }

                    override fun onError(result: ApiResult.Error<UpdateResponse>) {
                        _updateResult.postValue(result)
                    }

                })
        }


    }

    fun updateUser(user: User) {

        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(user)
        }
    }

    fun deleteAllUser() {

        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllUser()
        }

    }


}


