package com.example.fleurhaven.models

import com.google.gson.annotations.SerializedName

data class OrderItem(
    @SerializedName("flowerId") val flowerId: Int,
    @SerializedName("flower_name") val flower_name: String,  // Changed from "flower_name"
    @SerializedName("flower_price") val flower_price: Double, // Changed from "flower_price"
    @SerializedName("quantity") val quantity: Int
)

