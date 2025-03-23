package com.example.fleurhaven.models

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("userId") val userId: Int,  // Changed from "user_id"
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("phone_number") val phone_number: String,  // Changed from "phone_number"
    @SerializedName("address") val address: String,
    @SerializedName("items") val items: List<OrderItem>
)

