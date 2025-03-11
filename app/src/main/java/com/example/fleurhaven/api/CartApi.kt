package com.example.fleurhaven.api

import com.example.fleurhaven.models.CartItem
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CartApi {
    @Headers("Content-Type: application/json")
    @POST("add_to_cart.php")
    fun addToCart(@Body cartItem: CartItem): Call<Map<String, Any>>
}
