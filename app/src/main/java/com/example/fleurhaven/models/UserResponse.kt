package com.example.fleurhaven.models

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val success: Boolean,
    val message: String,
    val user: User?
)

data class User(
    val id: Int,
    val email: String
)
