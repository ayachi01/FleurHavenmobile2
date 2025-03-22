package com.example.fleurhaven.models

data class CheckoutResponse(
    val success: Boolean,
    val message: String,
    val order_id: Int? = null
)