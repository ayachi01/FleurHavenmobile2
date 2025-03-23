package com.example.fleurhaven

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fleurhaven.adapters.CheckoutAdapter
import com.example.fleurhaven.api.ApiService
import com.example.fleurhaven.api.RetrofitClient
import com.example.fleurhaven.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Activity_Checkout : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var address: EditText
    private lateinit var phone: EditText
    private lateinit var tvTotalPrice: TextView
    private lateinit var tvItemsCount: TextView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvFee: TextView
    private lateinit var tvTotalPayment: TextView
    private lateinit var recyclerCheckout: RecyclerView
    private lateinit var checkoutAdapter: CheckoutAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        apiService = RetrofitClient.apiService // ✅ API Service Initialization

        firstName = findViewById(R.id.et_fn)
        lastName = findViewById(R.id.et_ln)
        address = findViewById(R.id.et_address)
        phone = findViewById(R.id.et_phonenumber)
        tvTotalPrice = findViewById(R.id.tv_total_price)
        tvItemsCount = findViewById(R.id.tv_items_count)
        tvSubtotal = findViewById(R.id.tv_subtotal)
        tvFee = findViewById(R.id.tv_fee)
        tvTotalPayment = findViewById(R.id.tv_total_payment)
        recyclerCheckout = findViewById(R.id.recycler_checkout)

        recyclerCheckout.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        checkoutAdapter = CheckoutAdapter(this, arrayListOf())
        recyclerCheckout.adapter = checkoutAdapter

        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId == -1) {
            startActivity(Intent(this, Activity_Login::class.java))
            finish()
            return
        }

        firstName.setText(sharedPreferences.getString("first_name", ""))
        lastName.setText(sharedPreferences.getString("last_name", ""))
        address.setText(sharedPreferences.getString("address", ""))

        val cartItems = intent.getSerializableExtra("cartItems") as? ArrayList<CartItem>
        cartItems?.let { loadCheckoutItems(it) }

        findViewById<Button>(R.id.btn_place_order).setOnClickListener {
            placeOrder(userId, cartItems)
        }
    }

    private fun loadCheckoutItems(cartItems: List<CartItem>) {
        var totalItems = 0
        var subtotal = 0.0
        val fee = 50.0

        for (item in cartItems) {
            totalItems += item.quantity
            subtotal += item.flower_price * item.quantity
        }

        val totalPayment = subtotal + fee

        tvItemsCount.text = "Items: $totalItems"
        tvSubtotal.text = "Subtotal: ₱${"%.2f".format(subtotal)}"
        tvFee.text = "Fee: ₱${"%.2f".format(fee)}"
        tvTotalPayment.text = "Total Payment: ₱${"%.2f".format(totalPayment)}"
        tvTotalPrice.text = "Total: ₱${"%.2f".format(subtotal)}"

        checkoutAdapter.updateItems(cartItems)
    }

    private fun placeOrder(userId: Int, cartItems: ArrayList<CartItem>?) {
        if (cartItems.isNullOrEmpty()) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ Convert CartItem to OrderItem (fixing incorrect Item usage)
        val orderItems = cartItems.map { cartItem ->
            OrderItem(
                flowerId = cartItem.flower_id,
                flower_name = cartItem.flower_name, // Ensure cartItem has this field
                flower_price = cartItem.flower_price, // Ensure cartItem has this field
                quantity = cartItem.quantity
            )
        }


        val orderDetails = OrderRequest(
            userId = userId,
            firstName = firstName.text.toString().trim(),
            lastName = lastName.text.toString().trim(),
            address = address.text.toString().trim(),
            phone_number = phone.text.toString().trim(),
            items = orderItems // ✅ Correctly passing the list of OrderItems
        )

        apiService.placeOrder(orderDetails).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Activity_Checkout, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                    showOrderPlacedDialog()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@Activity_Checkout, "Failed: $errorBody", Toast.LENGTH_SHORT).show()
                    Log.e("ORDER_ERROR", "Response: $errorBody") // ✅ Debugging response errors
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                Toast.makeText(this@Activity_Checkout, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("NETWORK_ERROR", "Failure: ${t.message}") // ✅ Debugging network issues
            }
        })
    }

    private fun showOrderPlacedDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_order_placed)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.findViewById<Button>(R.id.btn_done).setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, Activity_Main::class.java))
        }

        dialog.show()
    }
}
