package com.example.fleurhaven

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fleurhaven.adapters.FlowerAdapter
import com.example.fleurhaven.api.FlowerApi
import com.example.fleurhaven.models.Flower
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class Activity_Main : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FlowerAdapter
    private lateinit var cartCountTextView: TextView
    private var userId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Retrieve user ID from Intent
        userId = intent.getIntExtra("user_id", -1)
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Activity_Login::class.java))
            finish()
            return
        }

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.flowerRecyclerView)
        adapter = FlowerAdapter(this, mutableListOf(), userId ?: -1) // Initialize with an empty list
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        // Navigation Bar Logic
        findViewById<ImageButton>(R.id.home_icon).setOnClickListener {
            startActivity(Intent(this, Activity_Main::class.java))
        }

        findViewById<ImageButton>(R.id.cart_icon).setOnClickListener {
            val intent = Intent(this, Activity_Cart::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.profile_icon).setOnClickListener {
            val intent = Intent(this, Activity_Profile::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }

        // Fetch flowers from API
        fetchFlowers()

        // Update Cart Count
        cartCountTextView = findViewById(R.id.cart_count)
        updateCartCount()
    }

    private fun fetchFlowers() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2/FleurHavenMobileAPI/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(FlowerApi::class.java)
        api.getFlowers().enqueue(object : Callback<List<Flower>> {
            override fun onResponse(call: Call<List<Flower>>, response: Response<List<Flower>>) {
                if (response.isSuccessful && response.body() != null) {
                    val flowers = response.body()!!
                    Log.d("API_RESPONSE", "Flowers loaded: ${flowers.size}")
                    adapter.updateFlowers(flowers)
                    Toast.makeText(this@Activity_Main, "Flowers loaded successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("API_ERROR", "Error loading flowers. Response code: ${response.code()}, Response body: ${response.errorBody()?.string()}")
                    Toast.makeText(this@Activity_Main, "Error loading flowers", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Flower>>, t: Throwable) {
                Log.e("API_ERROR", "Error: ${t.message}")
                Toast.makeText(this@Activity_Main, "Failed to load flowers: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateCartCount() {
        // Placeholder logic for cart count
        val cartItems = listOf<String>() // Replace with actual cart fetching logic
        runOnUiThread {
            cartCountTextView.text = cartItems.size.toString()
            cartCountTextView.visibility = if (cartItems.isNotEmpty()) View.VISIBLE else View.GONE
        }
        Log.d("CART_DEBUG", "Cart items for user $userId: $cartItems")
    }

    override fun onResume() {
        super.onResume()
        updateCartCount()
    }
}
