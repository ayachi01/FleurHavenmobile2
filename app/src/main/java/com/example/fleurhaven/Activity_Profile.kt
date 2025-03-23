package com.example.fleurhaven

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = getSharedPreferences("User_Profile", Context.MODE_PRIVATE)

        val profilePref = getSharedPreferences("user_data", MODE_PRIVATE)
        val userId = profilePref.getInt("user_id", -1)

        val homeIcon = findViewById<ImageButton>(R.id.home_icon)
        val cartIcon = findViewById<ImageButton>(R.id.cart_icon)
        val orderIcon = findViewById<ImageButton>(R.id.order_icon)
        addressTextView = findViewById(R.id.address_txt)
        firstNameTextView = findViewById(R.id.firstNameTxt)
        lastNameTextView = findViewById(R.id.lastNameTxt)
        emailTextView = findViewById(R.id.email_txt)
        val logoutButton = findViewById<Button>(R.id.LogoutButton)

        if (userId == -1) {
            redirectToLogin()
            return
        }

        fetchUserDetails(userId)

        homeIcon.setOnClickListener { navigateTo(Activity_Main::class.java) }
        orderIcon.setOnClickListener { navigateTo(Activity_OrderHistory::class.java) }

        cartIcon.setOnClickListener {
            val userAddress = addressTextView.text.toString()
            if (userAddress.isNullOrEmpty() || userAddress == "No address set") {
                showToast("Please set your address before proceeding to the cart.")
            } else {
                navigateTo(Activity_Cart::class.java)
            }
        }

        logoutButton.setOnClickListener { logoutUser() }
    }

    private fun fetchUserDetails(userId: Int) {
        val apiService = ApiClient.retrofit?.create(com.example.fleurhaven.api.ApiService::class.java)
        val call = apiService?.getUserDetails(userId)

        call?.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    if (userResponse != null && userResponse.success) {
                        firstNameTextView.text = userResponse.user?.firstName ?: "N/A"
                        lastNameTextView.text = userResponse.user?.lastName ?: "N/A"
                        emailTextView.text = userResponse.user?.email ?: "N/A"
                        addressTextView.text = userResponse.user?.address ?: "No address set"

                        saveUserDataToPrefs(userResponse)
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

    private fun saveUserDataToPrefs(userResponse: UserResponse) {
        val editor = getSharedPreferences("user_data", MODE_PRIVATE).edit()
        editor.putString("first_name", userResponse.user?.firstName)
        editor.putString("last_name", userResponse.user?.lastName)
        editor.putString("email", userResponse.user?.email)
        editor.putString("address", userResponse.user?.address)
        editor.apply()
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, Activity_Login::class.java))
        finish()
    }

    private fun navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun logoutUser() {
        sharedPreferences.edit().clear().apply()
        getSharedPreferences("user_data", MODE_PRIVATE).edit().clear().apply()

        showToast("Logged out successfully!")
        redirectToLogin()
    }
}
