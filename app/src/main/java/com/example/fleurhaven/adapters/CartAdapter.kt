package com.example.fleurhaven.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fleurhaven.R
import com.example.fleurhaven.api.ApiClient
import com.example.fleurhaven.api.ApiService
import com.example.fleurhaven.models.RemoveCartItemRequest
import com.example.fleurhaven.models.UpdateCartItemRequest
import com.example.fleurhaven.models.CartItem
import com.example.fleurhaven.models.ApiResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<CartItem>,
    private val userId: Int,
    private val onCartUpdated: (() -> Unit)? = null
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]

        holder.productName.text = cartItem.flower_name
        holder.productPrice.text = "₱ ${cartItem.flower_price}"
        holder.productQuantity.text = cartItem.quantity.toString()

        Glide.with(context)
            .load(cartItem.flower_image)
            .into(holder.productImage)

        holder.btnIncrease.setOnClickListener {
            val newQuantity = cartItem.quantity + 1
            updateCartItemQuantity(cartItem, position, newQuantity, holder)
        }

        holder.btnDecrease.setOnClickListener {
            val newQuantity = cartItem.quantity - 1
            if (newQuantity > 0) {
                updateCartItemQuantity(cartItem, position, newQuantity, holder)
            } else {
                removeCartItem(cartItem, position, holder)
            }
        }

        holder.btnClose.setOnClickListener {
            holder.btnClose.isEnabled = false
            removeCartItem(cartItem, position, holder)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    private fun updateCartItemQuantity(cartItem: CartItem, position: Int, newQuantity: Int, holder: CartViewHolder) {
        if (newQuantity <= 0 || position !in cartItems.indices) return

        val apiService = ApiClient.getClient().create(ApiService::class.java)
        val request = UpdateCartItemRequest(userId = userId, id = cartItem.id, quantity = newQuantity)

        apiService.updateCartItem(request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    cartItems[position].quantity = newQuantity
                    holder.productQuantity.text = newQuantity.toString()
                    notifyItemChanged(position)
                    onCartUpdated?.invoke()
                    Toast.makeText(context, "Quantity updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to update quantity", Toast.LENGTH_SHORT).show()
                    Log.e("CartAdapter", "Server error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.e("CartAdapter", "API call failed", t)
            }
        })
    }

    private fun removeCartItem(cartItem: CartItem, position: Int, holder: CartViewHolder) {
        val apiService = ApiClient.getClient().create(ApiService::class.java)
        val request = RemoveCartItemRequest(user_id = userId, id = cartItem.id, flower_id = cartItem.flower_id)

        apiService.removeCartItem(request).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                holder.btnClose.isEnabled = true

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == true) {
                        cartItems.removeAt(position)
                        notifyItemRemoved(position)
                        onCartUpdated?.invoke()
                        Toast.makeText(context, "Item removed successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, responseBody?.message ?: "Failed to remove item", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Server error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.e("CartAdapter", "Server error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                holder.btnClose.isEnabled = true
                Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.e("CartAdapter", "API call failed", t)
            }
        })
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.flower_name6)
        val productPrice: TextView = itemView.findViewById(R.id.flower_price6)
        val productQuantity: TextView = itemView.findViewById(R.id.tv_quantity6)
        val productImage: ImageView = itemView.findViewById(R.id.frame6)
        val btnIncrease: Button = itemView.findViewById(R.id.btn_increase6)
        val btnDecrease: Button = itemView.findViewById(R.id.btn_decrease6)
        val btnClose: ImageButton = itemView.findViewById(R.id.btn_close6)
    }
}
