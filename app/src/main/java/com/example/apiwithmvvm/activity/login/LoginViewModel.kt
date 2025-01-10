package com.example.apiwithmvvm.activity.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apitesting.basic.UtilityTools.Constants
import com.example.apiwithmvvm.model.LoginResponse
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
class LoginViewModel @Inject constructor(private val repository: LoginRepository) : ViewModel() {

    private val _validationResult = MutableLiveData<ValidationResult>()
    private val _loginResult = MutableLiveData<ApiResult<LoginResponse>>()

    val validationResult: LiveData<ValidationResult> get() = _validationResult
    val loginResult: LiveData<ApiResult<LoginResponse>> get() = _loginResult

    @Inject
    lateinit var validation:Validation

    fun validate(validationModelList: MutableList<ValidationModel>) {

        val validationResult = validation.CheckValidation(validationModelList)

        if (validationResult.validationCheck) {

            login(validationModelList[0].data, validationModelList[1].data)

        } else {

            _validationResult.value = validationResult

        }

    }

    private fun login(userName: String, password: String) {

        val hm = HashMap<String, String>()
        hm[Constants.userName] = userName
        hm[Constants.password] = password

        // Call the repository to sign up
        viewModelScope.launch(Dispatchers.IO) {

            _loginResult.postValue(ApiResult.Loading())

            // Observe the LiveData returned by the repository
            repository.login(hm, object : ApiCallback<LoginResponse> {

                override fun onSuccess(result: ApiResult.Success<LoginResponse>) {
                    _loginResult.postValue(result)
                }

                override fun onError(result: ApiResult.Error<LoginResponse>) {
                    _loginResult.postValue(result)
                }

            })
        }
    }

    fun insertUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUser(user)
        }
    }

}


