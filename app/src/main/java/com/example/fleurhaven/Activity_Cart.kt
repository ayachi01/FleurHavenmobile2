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
import android.util.Log

class Activity_Cart : AppCompatActivity() {

    private var totalAmount: Double = 0.0 // Variable to hold the total amount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val cartIcon = findViewById<ImageButton>(R.id.cart_icon)
        val profileIcon = findViewById<ImageButton>(R.id.profile_icon)
        val checkout = findViewById<Button>(R.id.checkoutBtn)

        val closeButton = findViewById<ImageButton>(R.id.btn_close)
        val closeButton2 = findViewById<ImageButton>(R.id.btn_close2)
        val closeButton3 = findViewById<ImageButton>(R.id.btn_close3)
        val cartItemQuantity = findViewById<TextView>(R.id.tv_quantity)
        val decreaseButton = findViewById<Button>(R.id.btn_decrease)
        val increaseButton = findViewById<Button>(R.id.btn_increase)
        val cartItemQuantity2 = findViewById<TextView>(R.id.tv_quantity2)
        val decreaseButton2 = findViewById<Button>(R.id.btn_decrease2)
        val increaseButton2 = findViewById<Button>(R.id.btn_increase2)
        val cartItemQuantity3 = findViewById<TextView>(R.id.tv_quantity3)
        val decreaseButton3 = findViewById<Button>(R.id.btn_decrease3)
        val increaseButton3 = findViewById<Button>(R.id.btn_increase3)

        val cartItemName1 = findViewById<TextView>(R.id.flower_name)
        val cartItemPrice1 = findViewById<TextView>(R.id.flower_price)
        val cartItemImage1 = findViewById<ImageView>(R.id.frame1)
        val cartItemName2 = findViewById<TextView>(R.id.flower_name2)
        val cartItemPrice2 = findViewById<TextView>(R.id.flower_price2)
        val cartItemImage2 = findViewById<ImageView>(R.id.frame2)
        val cartItemName3 = findViewById<TextView>(R.id.flower_name3)
        val cartItemPrice3 = findViewById<TextView>(R.id.flower_price3)
        val cartItemImage3 = findViewById<ImageView>(R.id.frame3)
        val tvAmount = findViewById<TextView>(R.id.tv_amount) // TextView for total amount

        closeButton.setOnClickListener {
            removeItemFromCart(0)
        }
        closeButton2.setOnClickListener {
            removeItemFromCart(1)
        }
        closeButton3.setOnClickListener {
            removeItemFromCart(2)
        }

        // Load cart items from SharedPreferences
        val sharedPreferences = getSharedPreferences("cart_data", Context.MODE_PRIVATE)
        val cartItemsString = sharedPreferences.getString("cart_items", "")

        if (!cartItemsString.isNullOrEmpty()) {
            val cartItems = cartItemsString.split(",")
            // Populate cart item views with data
            loadCartItems(cartItems)
        }

        // Set up quantity change listeners
        setupQuantityChangeListeners(cartItemQuantity, cartItemPrice1, 0)
        setupQuantityChangeListeners(cartItemQuantity2, cartItemPrice2, 1)
        setupQuantityChangeListeners(cartItemQuantity3, cartItemPrice3, 2)

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

    private fun setupQuantityChangeListeners(quantityTextView: TextView, priceTextView: TextView, index: Int) {
        val decreaseButton = when (index) {
            0 -> findViewById<Button>(R.id.btn_decrease)
            1 -> findViewById<Button>(R.id.btn_decrease2)
            2 -> findViewById<Button>(R.id.btn_decrease3)
            else -> null
        }

        val increaseButton = when (index) {
            0 -> findViewById<Button>(R.id.btn_increase)
            1 -> findViewById<Button>(R.id.btn_increase2)
            2 -> findViewById<Button>(R.id.btn_increase3)
            else -> null
        }

        decreaseButton?.setOnClickListener {
            val currentQuantity = quantityTextView.text.toString().toInt()
            if (currentQuantity > 1) {
                val newQuantity = currentQuantity - 1
                quantityTextView.text = newQuantity.toString()
                updateTotalAmount(priceTextView.text.toString(), -1) // Decrease total
            }
        }

        increaseButton?.setOnClickListener {
            val currentQuantity = quantityTextView.text.toString().toInt()
            val newQuantity = currentQuantity + 1
            quantityTextView.text = newQuantity.toString()
            updateTotalAmount(priceTextView.text.toString(), 1) // Increase total
        }
    }

    private fun updateTotalAmount(priceString: String, quantityChange: Int) {
        val price = extractPrice(priceString)
        totalAmount += price * quantityChange
        val tvAmount = findViewById<TextView>(R.id.tv_amount)
        tvAmount.text = "₱${String.format("%.2f", totalAmount)}"
    }

    private fun removeItemFromCart(index: Int) {
        val sharedPreferences = getSharedPreferences("cart_data", Context.MODE_PRIVATE)
        val cartItemsString = sharedPreferences.getString("cart_items", "")
        val cartItems = cartItemsString?.split(",")?.toMutableList()

        // Check if there are items in the cart and the index is valid
        if (!cartItems.isNullOrEmpty() && cartItems.size > index) {
            val itemToRemove = cartItems[index].split("|")
            if (itemToRemove.size > 1) {
                totalAmount -= extractPrice(itemToRemove[1]) // subtract price
            }
            cartItems.removeAt(index) // Remove item at the specified index
        }

        // Save the updated cart items back to SharedPreferences
        sharedPreferences.edit().putString("cart_items", cartItems?.joinToString(",")).apply()

        // Reload the cart items in the UI after removal
        loadCartItems(cartItems ?: mutableListOf())
    }

    private fun loadCartItems(cartItems: List<String>) {
        val cartItemName1 = findViewById<TextView>(R.id.flower_name)
        val cartItemPrice1 = findViewById<TextView>(R.id.flower_price)
        val cartItemImage1 = findViewById<ImageView>(R.id.frame1)
        val cartItemName2 = findViewById<TextView>(R.id.flower_name2)
        val cartItemPrice2 = findViewById<TextView>(R.id.flower_price2)
        val cartItemImage2 = findViewById<ImageView>(R.id.frame2)
        val cartItemName3 = findViewById<TextView>(R.id.flower_name3)
        val cartItemPrice3 = findViewById<TextView>(R.id.flower_price3)
        val cartItemImage3 = findViewById<ImageView>(R.id.frame3)
        val tvAmount = findViewById<TextView>(R.id.tv_amount)

        totalAmount = 0.0 // Reset total amount

        try {
            if (cartItems.isNotEmpty()) {
                val item1 = cartItems[0].split("|")
                cartItemName1.text = item1[0]
                cartItemPrice1.text = item1[1]
                cartItemImage1.setImageResource(item1[2].toInt())
                totalAmount += extractPrice(item1[1]) // add price to total
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
                totalAmount += extractPrice(item2[1])
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
                totalAmount += extractPrice(item3[1])
            } else {
                cartItemName3.text = ""
                cartItemPrice3.text = ""
                cartItemImage3.setImageDrawable(null)
            }

            // Update the total amount TextView
            val tvAmount = findViewById<TextView>(R.id.tv_amount)
            tvAmount.text = "₱${String.format("%.2f", totalAmount)}"
        } catch (e: Exception) {
            Log.e("Activity_Cart", "Error loading cart items: ${e.message}")
        }
    }

    private fun extractPrice(priceString: String): Double {
        return try {
            priceString.replace("₱", "").trim().toDouble()
        } catch (e: NumberFormatException) {
            Log.e("Activity_Cart", "Error parsing price: ${e.message}")
            0.0 // Return 0.0 if parsing fails
        }
    }

    private fun isAddressSet(): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences("User  _Profile", MODE_PRIVATE)
        val address = sharedPreferences.getString("address", null)
        return address != null
    }
}