package com.example.apiwithmvvm.fragment.allusers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apitesting.basic.UtilityTools.Utils
import com.example.apiwithmvvm.model.User
import com.example.apiwithmvvm.`interface`.ApiCallback
import com.example.apiwithmvvm.model.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllUsersViewModel @Inject constructor(private val repository: AllUsersRepository) : ViewModel() {

    private val _userList = MutableLiveData<ApiResult<List<User>>>()

    val userList: LiveData<ApiResult<List<User>>> get() = _userList

    private var showProgressBar = true

    fun getAllUsers() {

        viewModelScope.launch(Dispatchers.IO) {

            if (showProgressBar) {
                _userList.postValue(ApiResult.Loading())
                showProgressBar = false
            }

            // Observe the LiveData returned by the repository
            repository.getAllUsers(object : ApiCallback<List<User>> {

                override fun onSuccess(result: ApiResult.Success<List<User>>) {

                    // Extract the current and new user lists
                    val currentUsers = (userList.value as? ApiResult.Success<List<User>>)?.data ?: emptyList()
                    val newUsers = result.data?: emptyList()

                   // Check if the new user list is different from the current one, so the observer only triggers when the data changes.
                    if (!currentUsers.containsAll(newUsers) || !newUsers.containsAll(currentUsers)) {
                        _userList.postValue(result) // Update LiveData with new data
                    }

                }

                override fun onError(result: ApiResult.Error<List<User>>) {

                    _userList.postValue(result)
                }

            })
        }

    }

    fun deleteAllUser() {

        viewModelScope.launch {
            repository.deleteAllUser()
        }

    }
}