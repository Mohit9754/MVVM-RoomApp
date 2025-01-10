package com.example.apiwithmvvm.activity.splash

import com.example.apiwithmvvm.database.UserRepository
import javax.inject.Inject


class SplashRepository @Inject constructor(private val userRepository: UserRepository) {

    suspend fun isUserLogin(): Boolean {
        return userRepository.getUserCount() > 0
    }

}