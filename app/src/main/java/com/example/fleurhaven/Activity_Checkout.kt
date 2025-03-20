package com.example.fleurhaven

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Activity_Checkout : AppCompatActivity() {

    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var address: EditText
    private lateinit var phone: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        firstName = findViewById(R.id.et_fn)
        lastName = findViewById(R.id.et_ln)
        address = findViewById(R.id.et_address)
        phone = findViewById(R.id.et_phonenumber)

        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("email", null)
        val userPassword = sharedPreferences.getString("password", null)

        // Load saved user details
        val savedFirstName = sharedPreferences.getString("first_name", "")
        val savedLastName = sharedPreferences.getString("last_name", "")
        val savedAddress = sharedPreferences.getString("address", "")
        val savedPhone = sharedPreferences.getString("phone", "")

        if (!savedFirstName.isNullOrEmpty()) firstName.setText(savedFirstName)
        if (!savedLastName.isNullOrEmpty()) lastName.setText(savedLastName)
        if (!savedAddress.isNullOrEmpty()) address.setText(savedAddress)

        if (userEmail == null || userPassword == null) {
            startActivity(Intent(this, Activity_Login::class.java))
            finish()
            return
        }

        val cartItemsString = intent.getStringExtra("cart_items")
        if (!cartItemsString.isNullOrEmpty()) {
            val cartItems = cartItemsString.split(",")
            loadCheckoutItems(cartItems)
        }

        val homeIcon = findViewById<ImageButton>(R.id.home_icon)
        val cartIcon = findViewById<ImageButton>(R.id.cart_icon)
        val profileIcon = findViewById<ImageButton>(R.id.profile_icon)
        val orderIcon = findViewById<ImageButton>(R.id.order_icon)
        val btnPlaceOrder = findViewById<Button>(R.id.btn_place_order)

        homeIcon.setOnClickListener {
            startActivity(Intent(this, Activity_Main::class.java))
        }

        orderIcon.setOnClickListener {
            startActivity(Intent(this, Activity_OrderHistory::class.java))
        }

        cartIcon.setOnClickListener {
            startActivity(Intent(this, Activity_Cart::class.java))
        }

        profileIcon.setOnClickListener {
            val intent = if (userEmail != null && userPassword != null) {
                Intent(this, Activity_Profile::class.java)
            } else {
                Intent(this, Activity_Login::class.java)
            }
            startActivity(intent)
        }

        // Handle Place Order Button
        btnPlaceOrder.setOnClickListener {
            val firstNameInput = firstName.text.toString().trim()
            val lastNameInput = lastName.text.toString().trim()
            val addressInput = address.text.toString().trim()
            val phoneNumberInput = phone.text.toString().trim()

            // Input validation
            if (firstNameInput.isEmpty()) {
                firstName.error = "First name is required"
                return@setOnClickListener
            }
            if (lastNameInput.isEmpty()) {
                lastName.error = "Last name is required"
                return@setOnClickListener
            }
            if (addressInput.isEmpty()) {
                address.error = "Address is required"
                return@setOnClickListener
            }
            if (phoneNumberInput.isEmpty()) {
                phone.error = "Phone Number is required"
                return@setOnClickListener
            }
            if (!phoneNumberInput.matches(Regex("\\d+"))) {
                phone.error = "Phone Number must contain only digits"
                return@setOnClickListener
            }

            Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, Activity_OrderHistory::class.java))
        }
    }


    private fun loadCheckoutItems(cartItems: List<String>) {
        // Implement logic to display cart items in checkout page
    }

    private fun showOrderPlacedDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_order_placed)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnDone = dialog.findViewById<Button>(R.id.btn_done)
        btnDone.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, Activity_Main::class.java))
        }

        dialog.show()
    }
}