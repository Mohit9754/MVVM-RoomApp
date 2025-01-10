package com.example.apiwithmvvm.database

import javax.inject.Inject

class UserRepository @Inject constructor(private val userDao: UserDao) {

    // Suspend functions for CRUD operations
    suspend fun insertUser(user: User) {
        userDao.addUser(user)
    }

    suspend fun getUser(): User {
        return userDao.getUser()
    }

    suspend fun getUserCount(): Int {
        return userDao.getUserCount()
    }

    suspend fun updateUser(user: User): Int {
        return userDao.updateUser(user.name, user.email, user.profileImageUrl)
    }

    suspend fun deleteUser() {
        userDao.deleteUser()
    }
}
