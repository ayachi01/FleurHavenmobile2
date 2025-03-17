package com.example.fleurhaven.models

data class RemoveCartItemRequest(
    val user_id: Int,
    val id: Int,
    val flower_id: Int
)
