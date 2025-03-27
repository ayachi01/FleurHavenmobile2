package com.example.fleurhaven.api

import com.example.fleurhaven.models.SignupRequest
import com.example.fleurhaven.models.UserResponse
import com.example.fleurhaven.models.ApiResponse
import com.example.fleurhaven.models.CartItem
import com.example.fleurhaven.models.CartRequest
import com.example.fleurhaven.models.CheckoutRequest
import com.example.fleurhaven.models.CheckoutResponse
import com.example.fleurhaven.models.Flower
import com.example.fleurhaven.models.OrderHistoryResponse
import com.example.fleurhaven.models.OrderRequest
import com.example.fleurhaven.models.OrderResponse
import com.example.fleurhaven.models.OrderStatusUpdateRequest
import com.example.fleurhaven.models.RemoveCartItemRequest
import com.example.fleurhaven.models.UpdateAddressRequest
import com.example.fleurhaven.models.UpdateCartItemRequest
import com.example.fleurhaven.models.UserProfileResponse
import com.example.fleurhaven.models.UserUpdateRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
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

    @POST("update_cart_item.php")
    fun updateCartItem(@Body request: UpdateCartItemRequest): Call<ResponseBody>

    @POST("remove_cart_item.php")
    fun removeCartItem(@Body request: RemoveCartItemRequest): Call<ApiResponse>

    @POST("checkout.php")
    fun checkout(@Body request: CheckoutRequest): Call<CheckoutResponse>

    @GET("check_cart_item.php")
    fun checkCartItem(
        @Query("user_id") userId: Int,
        @Query("flower_id") flowerId: Int
    ): Call<ApiResponse>

    @POST("place_order.php")
    fun placeOrder(@Body orderRequest: OrderRequest): Call<OrderResponse>

    @GET("get_user_profile.php")
    fun getUserProfile(@Query("user_id") userId: Int): Call<UserProfileResponse>

    @GET("get_user.php")
    fun getUserDetails(@Query("user_id") userId: Int): Call<UserResponse>

    @POST("update_user.php")
    fun updateUser(@Body request: UserUpdateRequest): Call<ApiResponse>

    @GET("get_order_history.php")
    fun getOrderHistory(@Query("user_id") userId: Int): Call<OrderHistoryResponse>

    @POST("update_order_status.php")
    fun updateOrderStatus(@Body request: OrderStatusUpdateRequest): Call<ApiResponse>


}
