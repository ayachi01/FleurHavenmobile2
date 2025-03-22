package com.example.fleurhaven.models

import com.google.gson.annotations.SerializedName

data class UpdateCartItemRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("id") val id: Int, // Ensure this matches the API's expected field name
    @SerializedName("quantity") val quantity: Int? = null
)