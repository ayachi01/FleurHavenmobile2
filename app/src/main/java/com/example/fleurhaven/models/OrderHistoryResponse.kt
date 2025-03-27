package com.example.fleurhaven.models

data class OrderHistoryResponse(
    val success: Boolean,
    val orders: List<OrderResponse>
)
