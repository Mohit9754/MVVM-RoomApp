package com.example.apiwithmvvm.model

class LoginResponse(
    val message: String,
    val user: User
)

data class User(
    val name: String,
    val userName: String,
    val email: String,
    val token: String,
    val profileImageUrl: String
)