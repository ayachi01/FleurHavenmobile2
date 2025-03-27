package com.example.fleurhaven.models

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("order_id") val id: Int,
    @SerializedName("flower_name") val flowerName: String?,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("flower_price") val flowerPrice: String,  // Use String, since it may be returned as "1950.00"
    @SerializedName("image_url") val flowerImage: String?,
    @SerializedName("status") val status: String
)
