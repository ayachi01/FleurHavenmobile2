package com.example.fleurhaven.models

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val success: Boolean,
    val message: String,
    val user: User?
)

data class User(
    val id: Int,
    val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val address: String,
    @SerializedName("phone_number") val phoneNumber: String? // Make it nullable
)


