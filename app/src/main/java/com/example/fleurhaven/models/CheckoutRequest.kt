package com.example.fleurhaven.models

data class CheckoutRequest(
    val user_id: Int,
    val total_amount: Double,
    val items: List<Item>
)

data class Item(
    val flower_id: Int,
    val quantity: Int
)
