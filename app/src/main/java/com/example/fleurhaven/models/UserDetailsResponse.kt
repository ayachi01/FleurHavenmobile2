package com.example.fleurhaven.models

data class UserDetailsResponse(
    val success: Boolean,
    val message: String,
    val user: UserDetails? = null
)
