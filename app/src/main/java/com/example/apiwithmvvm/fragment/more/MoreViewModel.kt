package com.example.apiwithmvvm.fragment.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apiwithmvvm.model.User
import com.example.apiwithmvvm.`interface`.ApiCallback
import com.example.apiwithmvvm.model.ApiResponse
import com.example.apiwithmvvm.model.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(private val repository: MoreRepository) : ViewModel() {

    private val _deleteAccountResult = MutableLiveData<ApiResult<ApiResponse>>()

    val deleteAccountResult: LiveData<ApiResult<ApiResponse>> get() = _deleteAccountResult

    fun onDeleteAccountClick() {
        deleteAccount()
    }

    private fun deleteAccount() {

        viewModelScope.launch(Dispatchers.IO) {

            _deleteAccountResult.postValue(ApiResult.Loading())

            // Observe the LiveData returned by the repository
            repository.deleteAccount(object : ApiCallback<ApiResponse> {

                override fun onSuccess(result: ApiResult.Success<ApiResponse>) {

                    _deleteAccountResult.postValue(result)
                }

                override fun onError(result: ApiResult.Error<ApiResponse>) {

                    _deleteAccountResult.postValue(result)
                }

            })
        }

    }

    fun deleteUser() {

        viewModelScope.launch {
            repository.deleteUser()
        }

    }
}