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

        val homeIcon = findViewById<ImageButton>(R.id.home_icon)
        val cartIcon = findViewById<ImageButton>(R.id.cart_icon)
        val profileIcon = findViewById<ImageButton>(R.id.profile_icon)
        val addToCartBtn = findViewById<Button>(R.id.button1) // Rainbow Posies
        val addToCartBtn2 = findViewById<Button>(R.id.button2) // Daisy Kiss
        val addToCartBtn3 = findViewById<Button>(R.id.button3) // Just Because Flowers
        val addToCartBtn4 = findViewById<Button>(R.id.button4) // Pixie Posy
        val addToCartBtn5 = findViewById<Button>(R.id.button5) // Blush Bouquet
        val addToCartBtn6 = findViewById<Button>(R.id.button6) // Dangwa
        val addToCartBtn7 = findViewById<Button>(R.id.button7) // 11 Pink Bouquet
        val addToCartBtn8 = findViewById<Button>(R.id.button8) // Carnation
        val addToCartBtn9 = findViewById<Button>(R.id.button9) // Asteroid Destroyer
        val addToCartBtn10 = findViewById<Button>(R.id.button10) // Lilies Bouquet
        val addToCartBtn11 = findViewById<Button>(R.id.button11) // Valentines Bouquet
        val addToCartBtn12 = findViewById<Button>(R.id.button12) // White Roses Bouquet
        val addToCartBtn13 = findViewById<Button>(R.id.button13) // True Love Bouquet
        val addToCartBtn14 = findViewById<Button>(R.id.button14) // White Tulips
        val addToCartBtn15 = findViewById<Button>(R.id.button15) // White Daisies
        val addToCartBtn16 = findViewById<Button>(R.id.button16) // Orchids Bouquet
        val addToCartBtn17 = findViewById<Button>(R.id.button17) // Assorted Flowers
        val addToCartBtn18 = findViewById<Button>(R.id.button18) // Eternal Box Flowers
        cartCountTextView = findViewById(R.id.cart_count)

        updateCartCount()

        homeIcon.setOnClickListener {
            val intent = Intent(this, Activity_Main::class.java)
            startActivity(intent)
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

        addToCartBtn.setOnClickListener {
            addToCart("Rainbow Posies", "₱ 1000", R.drawable.rainbow_posies)
        }

        addToCartBtn2.setOnClickListener {
            addToCart("Daisy Kiss", "₱ 1000", R.drawable.daisykiss)
        }

        addToCartBtn3.setOnClickListener {
            addToCart("Just Because Flowers", "₱ 1000", R.drawable.flower_sample)
        }
        addToCartBtn4.setOnClickListener {
            addToCart("Pixie Posy Bouquet", "₱ 1000", R.drawable.pixie_posy)
        }
        addToCartBtn5.setOnClickListener {
            addToCart("Blush Bouquet", "₱ 1000", R.drawable.blush_bouquet)
        }
        addToCartBtn6.setOnClickListener {
            addToCart("Dangwa", "₱ 1000", R.drawable.dangwa)
        }
        addToCartBtn7.setOnClickListener {
            addToCart("11 Pink Bouquet", "₱ 1000", R.drawable.elevenpink)
        }
        addToCartBtn8.setOnClickListener {
            addToCart("Carnation", "₱ 1000", R.drawable.carnation)
        }
        addToCartBtn9.setOnClickListener {
            addToCart("Asteroid Destroyer", "₱ 1000", R.drawable.asteroid_destroyer)
        }
        addToCartBtn10.setOnClickListener {
            addToCart("Lilies Bouquet", "₱ 1000", R.drawable.lilies_bouquet)
        }
        addToCartBtn11.setOnClickListener {
            addToCart("Valentines Bouquet", "₱ 1000", R.drawable.valentine)
        }
        addToCartBtn12.setOnClickListener {
            addToCart("White Roses Bouquet", "₱ 1000", R.drawable.white_roses_bouquet)
        }
        addToCartBtn13.setOnClickListener {
            addToCart("True Love Bouquet", "₱ 1000", R.drawable.true_love)
        }
        addToCartBtn14.setOnClickListener {
            addToCart("White Tulips", "₱ 1000", R.drawable.white_tulips)
        }
        addToCartBtn15.setOnClickListener {
            addToCart("White Daisies", "₱ 1000", R.drawable.white_daisies)
        }
        addToCartBtn16.setOnClickListener {
            addToCart("Orchids Bouquet", "₱ 1000", R.drawable.boquet_of_orchids)
            }
        addToCartBtn17.setOnClickListener {
            addToCart("Assorted Flowers", "₱ 1000", R.drawable.assorted_flowers)
            }
        addToCartBtn18.setOnClickListener {
            addToCart("Eternal Box Flowers", "₱ 1000", R.drawable.eternal_heart_box)

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

        Toast.makeText(this, "$name added to cart", Toast.LENGTH_SHORT).show()

        updateCartCount()

    }

    private fun updateCartCount() {
        val sharedPreferences = getSharedPreferences("cart_data", Context.MODE_PRIVATE)
        val cartItemsString = sharedPreferences.getString("cart_items", "")
        val itemCount = cartItemsString?.split(",")?.filter { it.isNotEmpty() }?.size ?: 0

        if (itemCount > 0) {
            cartCountTextView.text = itemCount.toString() //  Set number
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
