package com.example.fleurhaven

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fleurhaven.api.RetrofitClient
import com.example.fleurhaven.models.UserResponse
import com.example.fleurhaven.models.SignupRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Activity_Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginBtn)
        val signUpText = findViewById<TextView>(R.id.signupText)

        // Redirect to Signup Activity
        signUpText.setOnClickListener {
            startActivity(Intent(this, Activity_Signup::class.java))
            finish()
        }

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Input validation
            if (email.isEmpty()) {
                emailEditText.error = "Email is required"
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.error = "Invalid email format"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordEditText.error = "Password is required"
                return@setOnClickListener
            }

            // Create SignupRequest object
            val request = SignupRequest(email, password)

            // Perform login request
            RetrofitClient.instance.loginUser(request)
                .enqueue(object : Callback<UserResponse> {
                    override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                        if (response.isSuccessful) {
                            val userResponse = response.body()
                            if (userResponse != null && userResponse.success) {
                                val userId = userResponse.user?.id
                                if (userId != null) {
                                    // Save the user ID in SharedPreferences
                                    val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                    with(sharedPreferences.edit()) {
                                        putInt("user_id", userId)
                                        apply()
                                    }

                                    Toast.makeText(this@Activity_Login, userResponse.message, Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@Activity_Login, Activity_Main::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this@Activity_Login, "Error: User ID is missing", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@Activity_Login, "Login failed: ${userResponse?.message}", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@Activity_Login, "Error: Incorrect Email or Password", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        Toast.makeText(this@Activity_Login, "Network error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                        Log.e("Activity_Login", "Login failed", t)
                    }
                })
        }
    }
}
