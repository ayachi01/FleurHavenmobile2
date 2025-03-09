package com.example.fleurhaven.models

import java.io.Serializable

data class CartItem(
    val name: String,
    val price: Double,
    var quantity: Int // Quantity is mutable
) : Serializable
