package com.example.apitesting.model

import com.example.apiwithmvvm.model.User

data class UpdateResponse(
    val message: String,
    val user: User
)