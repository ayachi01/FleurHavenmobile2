package com.example.fleurhaven

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fleurhaven.adapters.CartAdapter
import com.example.fleurhaven.api.ApiClient
import com.example.fleurhaven.api.ApiService
import com.example.fleurhaven.models.CartItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Activity_Cart : AppCompatActivity() {

    private lateinit var cartItems: MutableList<CartItem>
    private lateinit var cartAdapter: CartAdapter
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(this, "Please log in to view your cart.", Toast.LENGTH_SHORT).show()
            return
        }

        cartItems = mutableListOf()

        val recyclerView: RecyclerView = findViewById(R.id.cart_recyclerview)
        cartAdapter = CartAdapter(this, cartItems, userId) {
            updateCartSummary()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = cartAdapter

        fetchCartItems()
        setupNavigationBar()
        setupCheckoutButton()
    }

    private fun fetchCartItems() {
        val apiService = ApiClient.getClient().create(ApiService::class.java)
        val call = apiService.getCartItems(userId)

        call.enqueue(object : Callback<List<CartItem>> {
            override fun onResponse(call: Call<List<CartItem>>, response: Response<List<CartItem>>) {
                if (response.isSuccessful && response.body() != null) {
                    cartItems.clear()
                    cartItems.addAll(response.body()!!)
                    cartAdapter.notifyDataSetChanged()
                    updateCartSummary()
                } else {
                    Toast.makeText(this@Activity_Cart, "Failed to load cart items.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CartItem>>, t: Throwable) {
                Toast.makeText(this@Activity_Cart, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateCartSummary() {
        val cartCountTextView: TextView = findViewById(R.id.cart_count)
        cartCountTextView.text = cartItems.size.toString()
        cartCountTextView.visibility = if (cartItems.isNotEmpty()) View.VISIBLE else View.GONE

        val totalAmountTextView: TextView = findViewById(R.id.tv_amount)
        val totalAmount = cartItems.sumOf { it.flower_price * it.quantity }
        totalAmountTextView.text = "₱${"%.2f".format(totalAmount)}"
    }

    private fun setupCheckoutButton() {
        val checkoutButton: Button = findViewById(R.id.checkoutBtn)
        checkoutButton.setOnClickListener {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
            } else {
                val totalAmount = cartItems.sumOf { it.flower_price * it.quantity }
                val totalItems = cartItems.sumOf { it.quantity }

                val intent = Intent(this, Activity_Checkout::class.java).apply {
                    putExtra("TOTAL_AMOUNT", totalAmount)
                    putExtra("TOTAL_ITEMS", totalItems)
                    putExtra("cartItems", ArrayList(cartItems)) // Pass cartItems as Serializable
                }
                startActivity(intent)
            }
        }
    }

    private fun setupNavigationBar() {
        findViewById<ImageButton>(R.id.home_icon).setOnClickListener {
            startActivity(Intent(this, Activity_Main::class.java))
        }

        findViewById<ImageButton>(R.id.cart_icon).setOnClickListener {
            startActivity(Intent(this, Activity_Cart::class.java))
        }

        findViewById<ImageButton>(R.id.profile_icon).setOnClickListener {
            startActivity(Intent(this, Activity_Profile::class.java))
        }
    }
}
