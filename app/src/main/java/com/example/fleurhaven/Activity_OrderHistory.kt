package com.example.fleurhaven

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class Activity_OrderHistory : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orderhistory)

        val homeIcon = findViewById<ImageButton>(R.id.home_icon)
        val cartIcon = findViewById<ImageButton>(R.id.cart_icon)
        val profileIcon = findViewById<ImageButton>(R.id.profile_icon)
        val orderIcon = findViewById<ImageButton>(R.id.order_icon)

        homeIcon.setOnClickListener {
            val intent = Intent(this, Activity_Main::class.java)
            startActivity(intent)
        }

        orderIcon.setOnClickListener {
            val intent = Intent(this, Activity_OrderHistory::class.java)
            startActivity(intent)
        }

        cartIcon.setOnClickListener {
            val intent = Intent(this, Activity_Cart::class.java)
            startActivity(intent)
        }

        profileIcon.setOnClickListener {
            val intent = Intent(this, Activity_Profile::class.java)
            startActivity(intent)
        }
    }
}