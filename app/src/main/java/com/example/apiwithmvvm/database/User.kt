package com.example.apiwithmvvm.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")

data class User(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 1,
    var userName: String = "",
    var name: String = "",
    var email: String = "",
    var token: String = "",
    var profileImageUrl: String = ""

)