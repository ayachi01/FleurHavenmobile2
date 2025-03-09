package com.example.fleurhaven.api

import com.example.fleurhaven.models.Flower
import retrofit2.Call
import retrofit2.http.GET

interface FlowerApi {
    @GET("get_flowers.php")
    fun getFlowers(): Call<List<Flower>>
}
