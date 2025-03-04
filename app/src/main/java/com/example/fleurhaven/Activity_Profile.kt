package com.example.fleurhaven

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class Activity_Profile : AppCompatActivity() {

    private lateinit var addressTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("User  Profile", Context.MODE_PRIVATE)

        // Retrieve stored email from SharedPreferences
        val profilePref = getSharedPreferences("user_data", MODE_PRIVATE)
        val userEmail = profilePref.getString("email", null) // Null if not found
        val cartIcon = findViewById<ImageButton>(R.id.cart_icon)
        val editAddress = findViewById<ImageButton>(R.id.editaddress_icon)
        addressTextView = findViewById<TextView>(R.id.addresstxt) // Assuming this is the TextView for the address

        // Redirect to login if no email is found
        if (userEmail == null) {
            startActivity(Intent(this, Activity_Login::class.java))
            finish() // Close profile activity
            return
        }

        // Display email if found
        val emailTextView = findViewById<TextView>(R.id.emailtxt)
        emailTextView.text = userEmail

        // Load saved address
        loadAddress()

        // Set click listeners
        cartIcon.setOnClickListener {
            val savedAddress = sharedPreferences.getString("address", null)
            if (savedAddress == null) {
                Toast.makeText(this, "Please set your address before proceeding to the cart.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, Activity_Cart::class.java)
                startActivity(intent)
            }
        }

        editAddress.setOnClickListener {
            showSetAddressDialog()
        }
    }

    private fun showSetAddressDialog() {
        // Create an AlertDialog Builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Set Your Address")

        // Inflate the custom layout
        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.activity_set_address, null)
        builder.setView(dialogView)

        // Get references to the EditText and Button in the dialog
        val addressEditText = dialogView.findViewById<EditText>(R.id.addressEditText)
        val saveAddressButton = dialogView.findViewById<Button>(R.id.saveAddressButton)

        // Set the button click listener
        saveAddressButton.setOnClickListener {
            val address = addressEditText.text.toString()
            if (address.isNotEmpty()) {
                saveAddress(address)
                addressTextView.text = address // Update the TextView with the new address
                Toast.makeText(this, "Address saved!", Toast.LENGTH_SHORT).show()
            } else {
                addressEditText.error = "Address cannot be empty"
            }
        }

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun saveAddress(address: String) {
        val editor = sharedPreferences.edit()
        editor.putString("address", address)
        editor.apply()
        Toast.makeText(this, "Address saved: $address", Toast.LENGTH_SHORT).show()
    }

    private fun loadAddress() {
        val savedAddress = sharedPreferences.getString("address", null)
        if (savedAddress != null) {
            addressTextView.text = savedAddress // Set the TextView to the saved address
        }
    }
}