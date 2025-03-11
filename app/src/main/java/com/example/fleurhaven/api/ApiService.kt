package com.example.fleurhaven.api

import com.example.fleurhaven.models.SignupRequest
import com.example.fleurhaven.models.UserResponse
import com.example.fleurhaven.models.ApiResponse
import com.example.fleurhaven.models.CartItem
import com.example.fleurhaven.models.CartRequest
import com.example.fleurhaven.models.Flower
import com.example.fleurhaven.models.UpdateAddressRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("signup.php")
    fun registerUser(@Body request: SignupRequest): Call<UserResponse>

    @POST("login.php")
    fun loginUser(@Body request: SignupRequest): Call<UserResponse>

    @Headers("Content-Type: application/json") // Ensures JSON request format
    @POST("update_address.php")
    fun updateAddress(@Body request: UpdateAddressRequest): Call<ApiResponse>

    @GET("flowers")
    suspend fun getFlowers(): List<Flower>

    @Headers("Content-Type: application/json")
    @POST("add_to_cart.php")
    fun addToCart(@Body requestBody: CartRequest): Call<ApiResponse>

    @GET("get_cart_items.php")
    fun getCartItems(@Query("user_id") userId: Int): Call<List<CartItem>>

    @Headers("Content-Type: application/json")
    @POST("remove_cart_item.php")
    fun removeCartItem(@Body requestBody: Map<String, Any>): Call<ApiResponse>
}
