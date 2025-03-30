package com.example.fleurhaven.models

data class UserDetails(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val address: String,
    val phone_number: String // Added phone number
)
