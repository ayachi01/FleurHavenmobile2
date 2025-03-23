package com.example.fleurhaven

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.fleurhaven.api.RetrofitClient
import com.example.fleurhaven.models.UserResponse
import com.example.fleurhaven.models.SignupRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Activity_Login : AppCompatActivity() {
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginBtn)
        val signUpText = findViewById<TextView>(R.id.signupText)
        val passwordToggle = findViewById<ImageView>(R.id.passwordToggle)
        val errorMessage = findViewById<TextView>(R.id.errorMessage)

        // Password visibility toggle functionality
        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                passwordToggle.setImageResource(R.drawable.eye_open)
            } else {
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                passwordToggle.setImageResource(R.drawable.eye_close)
            }
            passwordEditText.setSelection(passwordEditText.text.length)
        }

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
            RetrofitClient.apiService.loginUser(request)
                .enqueue(object : Callback<UserResponse> {
                    override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                        if (response.isSuccessful) {
                            val userResponse = response.body()
                            if (userResponse != null && userResponse.success) {
                                val userId = userResponse.user?.id
                                if (userId != null) {
                                    // Save the user ID in SharedPreferences
                                    val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
                                    with(sharedPreferences.edit()) {
                                        putInt("user_id", userId) // Store user ID
                                        putString("email", email) // Store email
                                        apply()
                                    }
                                    errorMessage.visibility = View.GONE // Hide error message if login is correct
                                    Toast.makeText(this@Activity_Login, userResponse.message, Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@Activity_Login, Activity_Main::class.java))
                                    finish()
                                } else {
                                    errorMessage.visibility = View.VISIBLE
                                    errorMessage.text = "Error: User ID is missing"
                                }
                            } else {
                                errorMessage.visibility = View.VISIBLE
                                errorMessage.text = "Incorrect email or password"
                            }
                        } else {
                            errorMessage.visibility = View.VISIBLE
                            errorMessage.text = "Error: ${response.code()} - ${response.message()}"
                        }
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        errorMessage.visibility = View.VISIBLE
                        errorMessage.text = "Network error: ${t.localizedMessage}"
                        Log.e("Activity_Login", "Login failed", t)
                    }
                })
        }
    }
}
