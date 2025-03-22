package com.example.fleurhaven

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Activity_OrderHistory : AppCompatActivity() {

    private lateinit var statusList: List<TextView>
    private lateinit var cancelButton: Button
    private lateinit var receivedButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orderhistory)

        // Initialize navigation icons
        val homeIcon = findViewById<ImageButton>(R.id.home_icon)
        val cartIcon = findViewById<ImageButton>(R.id.cart_icon)
        val profileIcon = findViewById<ImageButton>(R.id.profile_icon)
        val orderIcon = findViewById<ImageButton>(R.id.order_icon)

        // Initialize status list and buttons
        statusList = listOf(
            findViewById(R.id.status_processing),
            findViewById(R.id.status_delivery),
            findViewById(R.id.status_completed),
            findViewById(R.id.status_canceled)
        )
        cancelButton = findViewById(R.id.cancel_button)
        receivedButton = findViewById(R.id.received_button)

        // Set click listener for status updates
        statusList.forEach { status ->
            status.setOnClickListener {
                highlightStatus(status)
            }
        }

        // Handle button clicks
        cancelButton.setOnClickListener {
            cancelOrder()
        }

        receivedButton.setOnClickListener {
            completeOrder()
        }

        // Handle navigation
        homeIcon.setOnClickListener {
            startActivity(Intent(this, Activity_Main::class.java))
        }

        cartIcon.setOnClickListener {
            startActivity(Intent(this, Activity_Cart::class.java))
        }

        profileIcon.setOnClickListener {
            startActivity(Intent(this, Activity_Profile::class.java))
        }

        orderIcon.setOnClickListener {
            startActivity(Intent(this, Activity_OrderHistory::class.java))
        }

        // Default to Processing status
        highlightStatus(findViewById(R.id.status_processing))
    }

    private fun highlightStatus(selectedStatus: TextView) {
        statusList.forEach { status ->
            if (status == selectedStatus) {
                status.setBackgroundResource(R.drawable.status_highlight)
                status.setTextColor(Color.WHITE)

                // Handle button visibility based on status
                when (status.id) {
                    R.id.status_processing -> {
                        cancelButton.visibility = View.VISIBLE
                        receivedButton.visibility = View.GONE
                    }
                    R.id.status_delivery -> {
                        cancelButton.visibility = View.GONE
                        receivedButton.visibility = View.VISIBLE
                    }
                    else -> {
                        cancelButton.visibility = View.GONE
                        receivedButton.visibility = View.GONE
                    }
                }
            } else {
                status.setBackgroundResource(R.drawable.status_background)
                status.setTextColor(Color.BLACK)
            }
        }
    }

    private fun cancelOrder() {
        highlightStatus(findViewById(R.id.status_canceled))
        Toast.makeText(this, "Order canceled", Toast.LENGTH_SHORT).show()
    }

    private fun completeOrder() {
        highlightStatus(findViewById(R.id.status_completed))
        Toast.makeText(this, "Order received", Toast.LENGTH_SHORT).show()
    }
}
