package com.example.fleurhaven

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.fleurhaven.api.ApiClient
import com.example.fleurhaven.models.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Activity_Profile : AppCompatActivity() {

    private lateinit var addressTextView: TextView
    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var emailTextView: TextView
    private var userId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userId = intent.getIntExtra("user_id", -1).takeIf { it != -1 }
        Log.d("ProfileActivity", "Received user_id: $userId")

        if (userId == null) {
            redirectToLogin()
            return
        }

        addressTextView = findViewById(R.id.address_txt)
        firstNameTextView = findViewById(R.id.firstNameTxt)
        lastNameTextView = findViewById(R.id.lastNameTxt)
        emailTextView = findViewById(R.id.email_txt)
        val logoutButton = findViewById<Button>(R.id.LogoutButton)

        fetchUserDetails(userId!!)
        setupNavigation()

        logoutButton.setOnClickListener { logoutUser() }
    }

    private fun setupNavigation() {
        findViewById<ImageButton>(R.id.home_icon).setOnClickListener {
            navigateTo(Activity_Main::class.java)
        }
        findViewById<ImageButton>(R.id.order_icon).setOnClickListener {
            navigateTo(Activity_OrderHistory::class.java)
        }
        findViewById<ImageButton>(R.id.cart_icon).setOnClickListener {
            val userAddress = addressTextView.text.toString()
            if (userAddress.isEmpty() || userAddress == "No address set") {
                showToast("Please set your address before proceeding to the cart.")
            } else {
                navigateTo(Activity_Cart::class.java)
            }
        }
    }

    private fun fetchUserDetails(userId: Int) {
        val apiService = ApiClient.retrofit?.create(com.example.fleurhaven.api.ApiService::class.java)
        val call = apiService?.getUserDetails(userId)

        call?.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    Log.d("ProfileActivity", "Fetched user: $userResponse")

                    if (userResponse != null && userResponse.success) {
                        firstNameTextView.text = userResponse.user?.firstName ?: "N/A"
                        lastNameTextView.text = userResponse.user?.lastName ?: "N/A"
                        emailTextView.text = userResponse.user?.email ?: "N/A"
                        addressTextView.text = userResponse.user?.address ?: "No address set"
                    } else {
                        showToast("Failed to retrieve user data")
                    }
                } else {
                    showToast("Server error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("ProfileActivity", "API Error: ${t.message}", t)
                showToast("Network error! Please try again.")
            }
        })
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, Activity_Login::class.java))
        finish()
    }

    private fun navigateTo(destination: Class<*>) {
        val intent = Intent(this, destination)
        intent.putExtra("user_id", userId)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun logoutUser() {
        showToast("Logged out successfully!")
        redirectToLogin()
    }
}
