package com.example.fleurhaven

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.example.fleurhaven.R


class Activity_Cart : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
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
            val intent = Intent(this, Activity_Login::class.java)
            startActivity(intent)
        }

        checkout.setOnClickListener {
            val intent = Intent(this, Activity_Checkout::class.java)
            startActivity(intent)
        }


    }
}