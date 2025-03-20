package com.example.fleurhaven.adapters

import com.example.fleurhaven.api.ApiService
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fleurhaven.Activity_Main
import com.example.fleurhaven.R
import com.example.fleurhaven.api.ApiClient
import com.example.fleurhaven.models.ApiResponse
import com.example.fleurhaven.models.CartRequest
import com.example.fleurhaven.models.Flower
import com.example.fleurhaven.models.UpdateCartItemRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlowerAdapter(private val context: Context, private var flowerList: MutableList<Flower>) :
    RecyclerView.Adapter<FlowerAdapter.ViewHolder>() {

    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val userId = sharedPreferences.getInt("user_id", -1) // Get userId as Int

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.flowerImage)
        val name: TextView = view.findViewById(R.id.flowerName)
        val price: TextView = view.findViewById(R.id.flowerPrice)
        val addToCart: Button = view.findViewById(R.id.addToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.flower_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val flower = flowerList[position]
        holder.name.text = flower.name
        holder.price.text = "₱${flower.price}"

        Glide.with(context)
            .load(flower.image_url.ifEmpty { "https://via.placeholder.com/150" })
            .into(holder.image)

        holder.addToCart.setOnClickListener {
            if (userId != -1) {
                checkCartAndAdd(flower)
            } else {
                Toast.makeText(context, "Please log in to add items to the cart.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = flowerList.size

    fun updateFlowers(newFlowers: List<Flower>) {
        flowerList.clear()
        flowerList.addAll(newFlowers)
        notifyDataSetChanged()
    }

    private fun checkCartAndAdd(flower: Flower) {
        val apiService = ApiClient.getClient().create(ApiService::class.java)

        apiService.checkCartItem(userId, flower.id).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val cartItemExists = response.body()?.success == true
                    val quantity = response.body()?.quantity ?: 0  // Get existing quantity

                    if (cartItemExists && quantity > 0) {
                        updateCartQuantity(flower, quantity) // Pass current quantity
                    } else {
                        addToCart(flower)
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun addToCart(flower: Flower) {
        val apiService = ApiClient.getClient().create(ApiService::class.java)

        val checkCall = apiService.checkCartItem(userId, flower.id)
        checkCall.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(context, "${flower.name} is already in the cart!", Toast.LENGTH_SHORT).show()
                } else {
                    // If the item is not in the cart, proceed to add it
                    val cartRequest = CartRequest(
                        user_id = userId,
                        flower_id = flower.id,
                        quantity = 1
                    )

                    val addCall = apiService.addToCart(cartRequest)
                    addCall.enqueue(object : Callback<ApiResponse> {
                        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                            if (response.isSuccessful && response.body()?.success == true) {
                                Toast.makeText(context, "${flower.name} added to cart!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to add to cart: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun updateCartQuantity(flower: Flower, currentQuantity: Int) {
        val apiService = ApiClient.getClient().create(ApiService::class.java)

        val newQuantity = currentQuantity + 1  // Ensure only 1 increment happens

        val updateRequest = UpdateCartItemRequest(
            userId = userId,
            id = flower.id,
            quantity = newQuantity
        )

        apiService.updateCartItem(updateRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Cart updated: $newQuantity items!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to update cart", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
