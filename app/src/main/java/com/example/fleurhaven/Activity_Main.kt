package com.example.fleurhaven

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Navigation Bar Logic
        val homeIcon: ImageButton = findViewById(R.id.home_icon)
        homeIcon.setOnClickListener {
            val intent = Intent(this, Activity_Main::class.java)
            startActivity(intent)
        }

        val cartIcon: ImageButton = findViewById(R.id.cart_icon)
        cartIcon.setOnClickListener {
            val intent = Intent(this, Activity_Cart::class.java)
            // Passing cart item count to Activity_Cart
            val sharedPreferences = getSharedPreferences("cart_data", Context.MODE_PRIVATE)
            val cartItemsString = sharedPreferences.getString("cart_items", "")
            val itemCount = cartItemsString?.split(",")?.filter { it.isNotEmpty() }?.size ?: 0
            intent.putExtra("cart_item_count", itemCount)
            startActivity(intent)
        }

        val profileIcon: ImageButton = findViewById(R.id.profile_icon)
        profileIcon.setOnClickListener {
            val intent = Intent(this, Activity_Profile::class.java)
            startActivity(intent)
        }

        // RecyclerView Setup
        recyclerView = findViewById(R.id.flowerRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Two-column grid
        adapter = FlowerAdapter(this, mutableListOf())
        recyclerView.adapter = adapter

        // Fetch flowers from API
        fetchFlowers()

        // Update Cart Count
        cartCountTextView = findViewById(R.id.cart_count)
        updateCartCount()
    }

    private fun fetchFlowers() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2/FleurHavenMobileAPI/") // Correct for Emulator
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
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "Failed: $errorBody")
                    Toast.makeText(this@Activity_Main, "Error loading flowers", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Flower>>, t: Throwable) {
                Log.e("API_ERROR", "Error: ${t.message}")
                Toast.makeText(this@Activity_Main, "Failed to load flowers: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun checkProfile() {
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val intent = if (sharedPreferences.contains("email")) {
            Intent(this, Activity_Profile::class.java)
        } else {
            Intent(this, Activity_Login::class.java)
        }
        startActivity(intent)
    }

    fun updateCartCount() {
        val sharedPreferences = getSharedPreferences("cart_data", Context.MODE_PRIVATE)
        val cartItemsString = sharedPreferences.getString("cart_items", "")
        val itemCount = cartItemsString?.split(",")?.filter { it.isNotEmpty() }?.size ?: 0

        if (itemCount > 0) {
            cartCountTextView.text = itemCount.toString()
            cartCountTextView.visibility = TextView.VISIBLE
        } else {
            cartCountTextView.visibility = TextView.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartCount()
    }
}
