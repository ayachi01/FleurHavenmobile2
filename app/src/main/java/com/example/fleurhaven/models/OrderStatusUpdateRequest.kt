package com.example.fleurhaven.models

data class OrderStatusUpdateRequest(
    val order_id: Int,
    val status: String
)
