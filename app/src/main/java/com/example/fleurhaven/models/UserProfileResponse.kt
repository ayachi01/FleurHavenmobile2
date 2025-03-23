package com.example.fleurhaven.models

data class UserProfileResponse(
    val success: Boolean,
    val user: UserProfile?
)

data class UserProfile(
    val first_name: String,
    val last_name: String,
    val address: String?
)
