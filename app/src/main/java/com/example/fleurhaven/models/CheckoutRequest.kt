package com.example.fleurhaven.models

data class CheckoutRequest(
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val address: String,
    val phone: String,
    val items: List<OrderItem> // ✅ Use Item instead of CartItem
)

data class Item(
    val flower_id: Int,
    val quantity: Int
)
