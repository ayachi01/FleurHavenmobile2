package com.example.fleurhaven

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class Activity_Checkout : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("email", null)
        val userPassword = sharedPreferences.getString("password", null)

        if (userEmail == null || userPassword == null) {
            startActivity(Intent(this, Activity_Login::class.java))
            finish() // Close checkout activity if not logged in
            return
        }

        // Retrieve cart data from Intent
        val cartItemsString = intent.getStringExtra("cart_items")
        if (!cartItemsString.isNullOrEmpty()) {
            val cartItems = cartItemsString.split(",")
            // Display cart items in checkout UI
            loadCheckoutItems(cartItems)
        }

        val homeIcon = findViewById<ImageButton>(R.id.home_icon)
        val cartIcon = findViewById<ImageButton>(R.id.cart_icon)
        val profileIcon = findViewById<ImageButton>(R.id.profile_icon)

        homeIcon.setOnClickListener {
            startActivity(Intent(this, Activity_Main::class.java))
        }

        cartIcon.setOnClickListener {
            startActivity(Intent(this, Activity_Cart::class.java))
        }

        profileIcon.setOnClickListener {
            val intent = if (userEmail != null && userPassword != null) {
                Intent(this, Activity_Profile::class.java) // Go to Profile if logged in
            } else {
                Intent(this, Activity_Login::class.java) // Otherwise, go to Login
            }
            startActivity(intent)
        }
    }

    private fun loadCheckoutItems(cartItems: List<String>) {
        // Implement logic to display cart items in checkout page
    }
}
