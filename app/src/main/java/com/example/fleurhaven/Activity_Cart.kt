package com.example.fleurhaven

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Activity_Cart : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val cartIcon = findViewById<ImageButton>(R.id.cart_icon)
        val profileIcon = findViewById<ImageButton>(R.id.profile_icon)
        val checkout = findViewById<Button>(R.id.checkoutBtn)

        cartIcon.setOnClickListener {
            val intent = Intent(this, Activity_Cart::class.java)
            startActivity(intent)
        }

        profileIcon.setOnClickListener {
            val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val storedEmail = sharedPreferences.getString("email", null)
            val storedPassword = sharedPreferences.getString("password", null)

            val intent = if (storedEmail != null && storedPassword != null) {
                Intent(this, Activity_Profile::class.java) // Go to Profile if logged in
            } else {
                Intent(this, Activity_Login::class.java) // Otherwise, go to Login
            }
            startActivity(intent)
        }

        checkout.setOnClickListener {
            if (!isAddressSet()) {
                // Address not set = go to Activity_Profile
                val intent = Intent(this, Activity_Profile::class.java)
                startActivity(intent)
            } else {
                // Address is set = proceed to Activity_Checkout
                val intent = Intent(this, Activity_Checkout::class.java)
                startActivity(intent)
            }
        }
    }

    private fun isAddressSet(): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences("User   Profile", MODE_PRIVATE)
        val address = sharedPreferences.getString("address", null)
        Toast.makeText(this, "Address confirmed!", Toast.LENGTH_SHORT).show()
        return address != null
    }
}