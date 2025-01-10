package com.example.apiwithmvvm.database

import androidx.room.*

@Dao
interface UserDao {

    @Insert
    suspend fun addUser(user: User): Long

    @Query("UPDATE user SET name = :name, email = :email, profileImageUrl = :profileImageUrl")
    suspend fun updateUser(name: String, email: String, profileImageUrl: String): Int

    @Query("SELECT * FROM user WHERE id = 1")
    fun getUser():User

    @Query("SELECT COUNT(*) FROM user")
    suspend fun getUserCount(): Int

    @Query("DELETE FROM user")
    suspend fun deleteUser()

}