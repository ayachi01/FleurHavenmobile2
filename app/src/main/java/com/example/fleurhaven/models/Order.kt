package com.example.fleurhaven.models

import com.google.gson.annotations.SerializedName

data class Order(
    val id: Int,
    val flowerName: String?,
    val flowerPrice: Double,
    val quantity: Int,
    var status: String?,
    @SerializedName("image_url") val image_url: String? // Ensure correct mapping
)


