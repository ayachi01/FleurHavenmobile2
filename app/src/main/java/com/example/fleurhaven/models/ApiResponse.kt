package com.example.fleurhaven.models

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val quantity: Int? = null
)

