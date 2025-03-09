package com.example.fleurhaven

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fleurhaven.adapters.CartAdapter
import com.example.fleurhaven.models.CartItem

class Activity_Cart : AppCompatActivity() {

    private lateinit var cartItems: MutableList<CartItem>
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Retrieve cart items from the intent
        cartItems = intent.getSerializableExtra("cart_items") as? MutableList<CartItem> ?: mutableListOf()

        // Set up RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.cart_recyclerview)
        cartAdapter = CartAdapter(this, cartItems)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = cartAdapter

        // Handle cart item count
        val cartCountTextView: TextView = findViewById(R.id.cart_count)
        cartCountTextView.text = cartItems.size.toString()
        cartCountTextView.visibility = if (cartItems.isNotEmpty()) View.VISIBLE else View.GONE

        // Handle bottom bar total amount
        val totalAmountTextView: TextView = findViewById(R.id.tv_amount)
        val totalAmount = cartItems.sumOf { it.price * it.quantity } // Sum of total price for all items
        totalAmountTextView.text = "₱${totalAmount}"

        // Bottom Navigation Bar Logic
        val homeIcon: ImageButton = findViewById(R.id.home_icon)
        homeIcon.setOnClickListener {
            val intent = Intent(this, Activity_Main::class.java)
            startActivity(intent)
        }

        val cartIcon: ImageButton = findViewById(R.id.cart_icon)
        cartIcon.setOnClickListener {
            val intent = Intent(this, Activity_Cart::class.java)
            intent.putExtra("cart_items", ArrayList(cartItems))  // Pass updated cart items back
            startActivity(intent)
        }

        val profileIcon: ImageButton = findViewById(R.id.profile_icon)
        profileIcon.setOnClickListener {
            val intent = Intent(this, Activity_Profile::class.java)
            startActivity(intent)
        }
    }
}
