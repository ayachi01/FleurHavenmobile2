package com.example.fleurhaven

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.fleurhaven.models.CartItem

class Activity_Checkout : AppCompatActivity() {

    private lateinit var tvItemsCount: TextView
    private lateinit var tvTotalPrice: TextView
    private lateinit var tvProductOrdered: TextView
    private lateinit var btnPlaceOrder: Button
    private lateinit var imgProduct: ImageView

    private var userEmail: String? = null
    private var userPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        // Initialize Views
        tvItemsCount = findViewById(R.id.tv_items_count)
        tvTotalPrice = findViewById(R.id.tv_total_price)
        tvProductOrdered = findViewById(R.id.tv_product_ordered)
        btnPlaceOrder = findViewById(R.id.btn_place_order)
        imgProduct = findViewById(R.id.img_product)

        // Load User Data
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        userEmail = sharedPreferences.getString("email", null)
        userPassword = sharedPreferences.getString("password", null)

        if (userEmail == null || userPassword == null) {
            startActivity(Intent(this, Activity_Login::class.java))
            finish() // Close checkout activity if not logged in
            return
        }

        // Retrieve Data from Intent
        val totalItems = intent.getIntExtra("TOTAL_ITEMS", 0)
        val totalAmount = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0)
        val cartItems = intent.getSerializableExtra("cartItems") as? ArrayList<CartItem> ?: arrayListOf()

        loadCheckoutItems(cartItems, totalItems, totalAmount)

        // Navigation Icons
        setupNavigation()

        // Place Order Button
        btnPlaceOrder.setOnClickListener {
            showOrderPlacedDialog()
        }
    }

    private fun loadCheckoutItems(cartItems: List<CartItem>, totalItems: Int, totalAmount: Double) {
        val productsOrdered = cartItems.joinToString("\n") { "${it.flower_name} x${it.quantity}" }

        tvItemsCount.text = "Items: $totalItems"
        tvTotalPrice.text = "Total: ₱%.2f".format(totalAmount)
        tvProductOrdered.text = "Products Ordered:\n$productsOrdered"

        // Load the first product image if available
        if (cartItems.isNotEmpty()) {
            val firstImageUrl = cartItems[0].flower_image
            Glide.with(this)
                .load(firstImageUrl)
                .placeholder(R.drawable.checkout_background)
                .error(R.drawable.checkout_background)
                .into(imgProduct)
        }
    }

    private fun setupNavigation() {
        findViewById<ImageButton>(R.id.home_icon).setOnClickListener {
            startActivity(Intent(this, Activity_Main::class.java))
        }

        findViewById<ImageButton>(R.id.order_icon).setOnClickListener {
            startActivity(Intent(this, Activity_OrderHistory::class.java))
        }

        findViewById<ImageButton>(R.id.cart_icon).setOnClickListener {
            startActivity(Intent(this, Activity_Cart::class.java))
        }

        findViewById<ImageButton>(R.id.profile_icon).setOnClickListener {
            val intent = if (userEmail != null && userPassword != null) {
                Intent(this, Activity_Profile::class.java)
            } else {
                Intent(this, Activity_Login::class.java)
            }
            startActivity(intent)
        }
    }

    private fun showOrderPlacedDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_order_placed)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnDone = dialog.findViewById<Button>(R.id.btn_done)
        btnDone.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Order Placed Successfully!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Activity_Main::class.java))
        }

        dialog.show()
    }
}
