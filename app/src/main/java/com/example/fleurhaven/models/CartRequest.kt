package com.example.fleurhaven.models

data class CartRequest(
    val user_id: Int,
    val flower_id: Int,
    val quantity: Int
)
