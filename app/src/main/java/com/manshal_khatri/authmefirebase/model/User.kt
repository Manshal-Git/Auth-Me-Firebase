package com.manshal_khatri.authmefirebase.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "userTable")
data class User(
    @PrimaryKey val emailId : String,
    val firstName : String,
    val lastName : String,
    val mobileNo : String ="",
    val address : String ="",
    val token : String? = "",
    val otp : String = ""
) : Serializable
