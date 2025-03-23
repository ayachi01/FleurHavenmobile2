package com.example.fleurhaven

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fleurhaven.api.RetrofitClient
import com.example.fleurhaven.models.SignupRequest
import com.example.fleurhaven.models.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Activity_Signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordEditText = findViewById<EditText>(R.id.passwconfEditText)
        val signupButton = findViewById<Button>(R.id.submitBtn) // Correct ID
        val loginText = findViewById<TextView>(R.id.signinText) // Correct ID

        // Redirect to Login Activity
        loginText.setOnClickListener {
            startActivity(Intent(this, Activity_Login::class.java))
            finish()
        }

        // Handle signup button click
        signupButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

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
            if (confirmPassword.isEmpty()) {
                confirmPasswordEditText.error = "Confirm password is required"
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                confirmPasswordEditText.error = "Passwords do not match"
                return@setOnClickListener
            }

            // Create SignupRequest object
            val request = SignupRequest(email, password)

            // Perform signup request
            RetrofitClient.apiService.registerUser(request)
                .enqueue(object : Callback<UserResponse> {
                    override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                        if (response.isSuccessful) {
                            val userResponse = response.body()
                            if (userResponse != null) {
                                if (userResponse.success) {
                                    Toast.makeText(this@Activity_Signup, userResponse.message, Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@Activity_Signup, Activity_Login::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this@Activity_Signup, "Signup failed: ${userResponse.message}", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@Activity_Signup, "Error: Empty response from server", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@Activity_Signup, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        Toast.makeText(this@Activity_Signup, "Network error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                        Log.e("Activity_Signup", "Signup failed", t)
                    }
                })
        }
    }
}
