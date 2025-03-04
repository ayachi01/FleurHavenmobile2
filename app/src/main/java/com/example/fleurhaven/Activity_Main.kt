package com.example.fleurhaven

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Activity_Main : AppCompatActivity() {

    private lateinit var cartCountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cartIcon = findViewById<ImageButton>(R.id.cart_icon)
        val profileIcon = findViewById<ImageButton>(R.id.profile_icon)
        val addToCartBtn = findViewById<Button>(R.id.button1) // Rainbow Posies
        val addToCartBtn2 = findViewById<Button>(R.id.button2) // Daisy Kiss
        val addToCartBtn3 = findViewById<Button>(R.id.button3) // Just Because Flowers
        cartCountTextView = findViewById(R.id.cart_count)

        updateCartCount()

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

        addToCartBtn.setOnClickListener {
            addToCartIfAddressSet("Rainbow Posies", "₱ 1000", R.drawable.rainbow_posies)
        }

        addToCartBtn2.setOnClickListener {
            addToCartIfAddressSet("Daisy Kiss", "₱ 1000", R.drawable.daisykiss)
        }

        addToCartBtn3.setOnClickListener {
            addToCartIfAddressSet("Just Because Flowers", "₱ 1000", R.drawable.flower_sample)
        }
    }

    private fun addToCartIfAddressSet(name: String, price: String, imageResId: Int) {
        val sharedPreferences = getSharedPreferences("User    Profile", Context.MODE_PRIVATE)
        val savedAddress = sharedPreferences.getString("address", null)

        if (savedAddress == null) {
            Toast.makeText(this, "Please set your address in your profile.", Toast.LENGTH_SHORT).show()
            // Optionally, redirect to the profile activity
            val intent = Intent(this, Activity_Profile::class.java)
            startActivity(intent)
        } else {
            addToCart(name, price, imageResId)
        }
    }

    private fun addToCart(name: String, price: String, imageResId: Int) {
        val sharedPreferences = getSharedPreferences("cart_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val cartString = sharedPreferences.getString("cart_items", "") ?: ""
        val newItem = "$name|$price|$imageResId"
        val updatedCart = if (cartString.isNotEmpty()) "$cartString,$newItem" else newItem

        editor.putString("cart_items", updatedCart)
        editor.apply()

        updateCartCount()
    }

    private fun updateCartCount() {
        val sharedPreferences = getSharedPreferences("cart_data", Context.MODE_PRIVATE)
        val cartItemsString = sharedPreferences.getString("cart_items", "")
        val itemCount = cartItemsString?.split(",")?.filter { it.isNotEmpty() }?.size ?: 0

        if (itemCount > 0) {
            cartCountTextView.text = itemCount.toString() // Set number
            cartCountTextView.visibility = TextView.VISIBLE
        } else {
            cartCountTextView.visibility = TextView.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartCount() // Update cart count when returning to main screen
    }
}