package com.example.fleurhaven

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences

class Activity_Cart : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val cartIcon = findViewById<ImageButton>(R.id.cart_icon)
        val profileIcon = findViewById<ImageButton>(R.id.profile_icon)
        val checkout = findViewById<Button>(R.id.checkoutBtn)

        val cartItemName1 = findViewById<TextView>(R.id.flower_name)
        val cartItemPrice1 = findViewById<TextView>(R.id.flower_price)
        val cartItemImage1 = findViewById<ImageView>(R.id.frame1)
        val cartItemName2 = findViewById<TextView>(R.id.flower_name2)
        val cartItemPrice2 = findViewById<TextView>(R.id.flower_price2)
        val cartItemImage2 = findViewById<ImageView>(R.id.frame2)
        val cartItemName3 = findViewById<TextView>(R.id.flower_name3)
        val cartItemPrice3 = findViewById<TextView>(R.id.flower_price3)
        val cartItemImage3 = findViewById<ImageView>(R.id.frame3)


        val sharedPreferences = getSharedPreferences("cart_data", Context.MODE_PRIVATE)
        val cartItemsString = sharedPreferences.getString("cart_items", "")

        if (!cartItemsString.isNullOrEmpty()) {
            val cartItems = cartItemsString.split(",")

            if (cartItems.isNotEmpty()) {
                val item1 = cartItems[0].split("|")
                cartItemName1.text = item1[0]
                cartItemPrice1.text = item1[1]
                cartItemImage1.setImageResource(item1[2].toInt())
            } else {
                cartItemName1.text = ""
                cartItemPrice1.text = ""
                cartItemImage1.setImageDrawable(null)
            }

            if (cartItems.size > 1) {
                val item2 = cartItems[1].split("|")
                cartItemName2.text = item2[0]
                cartItemPrice2.text = item2[1]
                cartItemImage2.setImageResource(item2[2].toInt())
            } else {
                cartItemName2.text = ""
                cartItemPrice2.text = ""
                cartItemImage2.setImageDrawable(null)
            }

            if (cartItems.size > 2) {
                val item3 = cartItems[2].split("|")
                cartItemName3.text = item3[0]
                cartItemPrice3.text = item3[1]
                cartItemImage3.setImageResource(item3[2].toInt())
            } else {
                cartItemName3.text = ""
                cartItemPrice3.text = ""
                cartItemImage3.setImageDrawable(null)
            }
        }

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
                val intent = Intent(this, Activity_Profile::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, Activity_Checkout::class.java)
                startActivity(intent)
            }
        }
    }

    private fun isAddressSet(): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences("User_Profile", MODE_PRIVATE)
        val address = sharedPreferences.getString("address", null)
        return address != null
    }
}
