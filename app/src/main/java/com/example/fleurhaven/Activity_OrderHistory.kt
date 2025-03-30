package com.example.fleurhaven

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fleurhaven.adapters.OrderHistoryAdapter
import com.example.fleurhaven.api.RetrofitClient
import com.example.fleurhaven.models.Order
import com.example.fleurhaven.models.OrderHistoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Activity_OrderHistory : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderHistoryAdapter
    private var userId: Int = -1
    private var allOrders: List<Order> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orderhistory)

        // Retrieve user ID from Intent
        userId = intent.getIntExtra("user_id", -1)
        Log.d("DEBUG", "Retrieved user_id: $userId")

        if (userId == -1) {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewOrders)
        recyclerView.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderHistoryAdapter(this, mutableListOf(), {}, {})
        recyclerView.adapter = orderAdapter

        // Fetch order history
        fetchOrderHistory()

        // Setup navigation and filters
        setupNavigation()
        setupStatusFilters()
    }

    private fun fetchOrderHistory() {
        val apiService = RetrofitClient.apiService
        apiService.getOrderHistory(userId).enqueue(object : Callback<OrderHistoryResponse> {
            override fun onResponse(call: Call<OrderHistoryResponse>, response: Response<OrderHistoryResponse>) {
                if (response.isSuccessful) {
                    val orderHistoryResponse = response.body()

                    if (orderHistoryResponse != null && orderHistoryResponse.orders.isNotEmpty()) {
                        val updatedOrders = orderHistoryResponse.orders.map { orderResponse ->
                            Order(
                                id = orderResponse.id,
                                flowerName = orderResponse.flowerName ?: "Unknown",
                                flowerPrice = orderResponse.flowerPrice.toDoubleOrNull() ?: 0.0,
                                quantity = orderResponse.quantity,
                                image_url = orderResponse.flowerImage,
                                status = orderResponse.status
                            )
                        }
                        allOrders = updatedOrders

                        // Filter only "Processing" orders by default
                        val processingOrders = updatedOrders.filter { it.status?.trim()?.equals("Processing", ignoreCase = true) == true }
                        orderAdapter.updateOrders(processingOrders)
                    } else {
                        Toast.makeText(this@Activity_OrderHistory, "No orders found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Activity_OrderHistory, "Failed to fetch order history", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderHistoryResponse>, t: Throwable) {
                Toast.makeText(this@Activity_OrderHistory, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupStatusFilters() {
        val processing = findViewById<TextView>(R.id.status_processing)
        val delivery = findViewById<TextView>(R.id.status_delivery)
        val completed = findViewById<TextView>(R.id.status_completed)
        val cancelled = findViewById<TextView>(R.id.status_canceled)

        val statusTabs = listOf(processing, delivery, completed, cancelled)

        statusTabs.forEach { tab ->
            tab.setOnClickListener {
                statusTabs.forEach {
                    it.setBackgroundResource(R.drawable.status_background)
                    it.setTextColor(ContextCompat.getColor(this, R.color.black))
                }
                tab.setBackgroundResource(R.drawable.status_highlight)
                tab.setTextColor(ContextCompat.getColor(this, R.color.white))

                val selectedStatus = tab.text.toString().trim()
                Log.d("DEBUG", "Selected Status: $selectedStatus")
                filterOrders(selectedStatus)
            }
        }

        processing.setBackgroundResource(R.drawable.status_highlight)
        processing.setTextColor(ContextCompat.getColor(this, R.color.white))
        filterOrders("Processing")
    }

    private fun filterOrders(status: String) {
        val filteredList = allOrders.filter { it.status?.trim()?.equals(status.trim(), ignoreCase = true) == true }
        Log.d("DEBUG", "Filtering by status: $status")
        Log.d("DEBUG", "Filtered Orders Count: ${filteredList.size}")

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No orders found for $status", Toast.LENGTH_SHORT).show()
        }

        orderAdapter.updateOrders(filteredList)
    }

    private fun setupNavigation() {
        findViewById<ImageButton>(R.id.home_icon).setOnClickListener {
            startActivity(Intent(this, Activity_Main::class.java).apply {
                putExtra("user_id", userId)
            })
        }
        findViewById<ImageButton>(R.id.cart_icon).setOnClickListener {
            startActivity(Intent(this, Activity_Cart::class.java).apply {
                putExtra("user_id", userId)
            })
        }
        findViewById<ImageButton>(R.id.profile_icon).setOnClickListener {
            startActivity(Intent(this, Activity_Profile::class.java).apply {
                putExtra("user_id", userId)
            })
        }
        findViewById<ImageButton>(R.id.order_icon).setOnClickListener {
            startActivity(Intent(this, Activity_OrderHistory::class.java).apply {
                putExtra("user_id", userId)
            })
        }
    }
}
