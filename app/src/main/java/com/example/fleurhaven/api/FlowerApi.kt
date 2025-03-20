package com.example.fleurhaven.api

import com.example.fleurhaven.models.ApiResponse
import com.example.fleurhaven.models.Flower
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface FlowerApi {
    @GET("get_flowers.php")
    fun getFlowers(): Call<List<Flower>>

    @FormUrlEncoded
    @POST("add_to_cart.php")
    fun addToCart(
        @Field("user_id") userId: Int,
        @Field("flower_id") flowerId: Int,
        @Field("quantity") quantity: Int
    ): Call<ApiResponse>

}
