package com.example.fleurhaven

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fleurhaven.adapters.CheckoutAdapter
import com.example.fleurhaven.api.ApiClient
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
    private var cartItems: ArrayList<CartItem> = arrayListOf()
    private var userId: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        apiService = RetrofitClient.apiService

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

        userId = intent.getIntExtra("user_id", -1)
        if (userId == -1) {
            startActivity(Intent(this, Activity_Login::class.java))
            finish()
            return
        }

        // Load user details from API
        loadUserDetailsFromDatabase()

        intent.getSerializableExtra("cartItems")?.let {
            cartItems = it as ArrayList<CartItem>
            loadCheckoutItems(cartItems)
        }

        findViewById<Button>(R.id.btn_place_order).setOnClickListener {
            placeOrder()
        }
    }

    private fun loadUserDetailsFromDatabase() {
        apiService.getUserDetails(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.user
                    if (user != null) {
                        // Populate the fields with the user details
                        firstName.setText(user.firstName)  // Use camelCase
                        lastName.setText(user.lastName)    // Use camelCase
                        address.setText(user.address)
                        phone.setText(user.phoneNumber)    // Set phone_number
                    }
                } else {
                    Toast.makeText(this@Activity_Checkout, "Failed to load user details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@Activity_Checkout, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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

    private fun updateUserDetails() {
        val updatedFirstName = firstName.text.toString().trim()
        val updatedLastName = lastName.text.toString().trim()
        val updatedAddress = address.text.toString().trim()
        val updatedPhoneNumber = phone.text.toString().trim()

        if (updatedFirstName.isEmpty() || updatedLastName.isEmpty() || updatedAddress.isEmpty() || updatedPhoneNumber.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        val userDetails = UserUpdateRequest(
            userId = userId,
            firstName = updatedFirstName,
            lastName = updatedLastName,
            address = updatedAddress,
            phone_number = updatedPhoneNumber
        )

        apiService.updateUser(userDetails).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@Activity_Checkout, "User details updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Activity_Checkout, "Failed to update user details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@Activity_Checkout, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun placeOrder() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show()
            return
        }

        // Update user details first
        updateUserDetails()

        // Now place the order
        val orderItems = cartItems.map { cartItem ->
            OrderItem(
                flowerId = cartItem.flower_id,
                flower_name = cartItem.flower_name,
                flower_price = cartItem.flower_price,
                quantity = cartItem.quantity
            )
        }

        val orderDetails = OrderRequest(
            userId = userId,
            firstName = firstName.text.toString().trim(),
            lastName = lastName.text.toString().trim(),
            address = address.text.toString().trim(),
            phone_number = phone.text.toString().trim(),
            items = orderItems
        )

        apiService.placeOrder(orderDetails).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Activity_Checkout, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                    clearCart()
                    showOrderPlacedDialog()
                } else {
                    Toast.makeText(this@Activity_Checkout, "Order failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                Toast.makeText(this@Activity_Checkout, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearCart() {
        apiService.clearCart(userId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    cartItems.clear()
                    checkoutAdapter.updateItems(cartItems)
                    Toast.makeText(this@Activity_Checkout, "Cart cleared!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Activity_Checkout, "Failed to clear cart!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@Activity_Checkout, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showOrderPlacedDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_order_placed)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.findViewById<Button>(R.id.btn_done).setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, Activity_Main::class.java)
            intent.putExtra("user_id", userId) // ✅ Pass user_id to avoid logout issue
            startActivity(intent)
            finish()
        }

        dialog.show()
    }
}
