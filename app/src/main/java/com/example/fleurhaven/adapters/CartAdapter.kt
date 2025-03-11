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
import com.example.fleurhaven.models.ApiResponse
import com.example.fleurhaven.models.CartItem
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
            cartItems[position] = cartItem.copy(quantity = cartItem.quantity + 1)
            holder.productQuantity.text = cartItems[position].quantity.toString()
            onCartUpdated?.invoke()
            notifyItemChanged(position)
        }

        holder.btnDecrease.setOnClickListener {
            if (cartItem.quantity > 1) {
                cartItems[position] = cartItem.copy(quantity = cartItem.quantity - 1)
                holder.productQuantity.text = cartItems[position].quantity.toString()
                onCartUpdated?.invoke()
                notifyItemChanged(position)
            }
        }

        holder.btnClose.setOnClickListener {
            removeCartItem(cartItem, position)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    private fun removeCartItem(cartItem: CartItem, position: Int) {
        val apiService = ApiClient.getClient().create(ApiService::class.java)

        val requestBody = mapOf(
            "user_id" to userId,
            "cart_item_id" to cartItem.id,
            "flower_id" to cartItem.flower_id  // Add this line
        )

        Log.d("CartAdapter", "Sending Request Body: $requestBody")

        val call = apiService.removeCartItem(requestBody)

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    cartItems.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, cartItems.size)
                    onCartUpdated?.invoke()
                    Toast.makeText(context, "Item removed from cart.", Toast.LENGTH_SHORT).show()
                    Log.d("CartAdapter", "Item removed successfully.")
                } else {
                    val errorMsg = response.body()?.message ?: "Failed to remove item."
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    Log.e("CartAdapter", "Failed to remove item: $errorMsg")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("CartAdapter", "Error removing item: ${t.message}")
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
