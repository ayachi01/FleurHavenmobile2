package com.example.fleurhaven

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Activity_Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Retrieve stored email from SharedPreferences
        val profilePref = getSharedPreferences("user_data", MODE_PRIVATE)
        val userEmail = profilePref.getString("email", null) // Null if not found
        val cartIcon = findViewById<ImageButton>(R.id.cart_icon)

        cartIcon.setOnClickListener {
            val intent = Intent(this, Activity_Cart::class.java)
            startActivity(intent)
        }

        // Redirect to login if no email is found
        if (userEmail == null) {
            startActivity(Intent(this, Activity_Login::class.java))
            finish() // Close profile activity
            return
        }

        // Display email if found
        val emailTextView = findViewById<TextView>(R.id.emailtxt)
        emailTextView.text = userEmail
    }
}
