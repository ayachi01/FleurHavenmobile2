package com.example.fleurhaven.models

import java.io.Serializable

data class OrderItem(
    val name: String,
    val price: Double,
    val quantity: Int
) : Serializable
