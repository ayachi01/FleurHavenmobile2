package com.example.fleurhaven.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CartItem(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("flower_id") val flower_id: Int,
    @SerializedName("quantity") var quantity: Int,
    @SerializedName("added_at") val addedAt: String,
    @SerializedName("flower_name") val flower_name: String,
    @SerializedName("flower_price") val flower_price: Double,
    @SerializedName("flower_image") val flower_image: String
) : Serializable



