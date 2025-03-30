package com.example.fleurhaven.models

data class UserDetailsUpdate(
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val address: String,
    val phoneNumber: String
)
