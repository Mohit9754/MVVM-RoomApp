package com.example.apiwithmvvm.activity.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val repository: SplashRepository) : ViewModel() {

    suspend fun isUserLogin(): Boolean {

        val result = viewModelScope.async(Dispatchers.IO) {
            return@async repository.isUserLogin()
        }

        return result.await()
    }


}