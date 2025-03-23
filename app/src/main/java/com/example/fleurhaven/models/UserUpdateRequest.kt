package com.example.fleurhaven.models

data class UserUpdateRequest(
    val firstName: String,
    val lastName: String,
    val address: String,
    val phone: String,
    val userId: Int
)
