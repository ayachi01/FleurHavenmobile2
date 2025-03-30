package com.example.fleurhaven

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.fleurhaven.api.RetrofitClient
import com.example.fleurhaven.models.SignupRequest
import com.example.fleurhaven.models.UserResponse
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Activity_Signup : AppCompatActivity() {
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordEditText = findViewById<EditText>(R.id.passwconfEditText)
        val signupButton = findViewById<Button>(R.id.submitBtn)
        val loginText = findViewById<TextView>(R.id.signinText)
        val passwordLayout = findViewById<TextInputLayout>(R.id.passwordToggle)
        val confirmPasswordToggle = findViewById<TextInputLayout>(R.id.confirmPasswordToggle)

        passwordLayout.setEndIconOnClickListener {
            togglePasswordVisibility(passwordEditText) // ✅ Only pass EditText
        }

        confirmPasswordToggle.setOnClickListener {
            togglePasswordVisibility(confirmPasswordEditText) // ✅ No extra argument
        }

        loginText.setOnClickListener {
            startActivity(Intent(this, Activity_Login::class.java))
            finish()
        }

        signupButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (!validateInput(email, password, confirmPassword, emailEditText, passwordEditText, confirmPasswordEditText)) return@setOnClickListener
            performSignup(email, password)
        }
    }

    private fun togglePasswordVisibility(editText: EditText) {
        val isVisible = editText.transformationMethod !is PasswordTransformationMethod
        editText.transformationMethod = if (isVisible) PasswordTransformationMethod.getInstance() else HideReturnsTransformationMethod.getInstance()
        editText.setSelection(editText.text.length) // Keeps cursor at the end
    }

    private fun validateInput(email: String, password: String, confirmPassword: String, emailEditText: EditText, passwordEditText: EditText, confirmPasswordEditText: EditText): Boolean {
        if (email.isEmpty()) {
            emailEditText.error = "Email is required"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Invalid email format"
            return false
        }
        if (password.isEmpty()) {
            passwordEditText.error = "Password is required"
            return false
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.error = "Confirm password is required"
            return false
        }
        if (password != confirmPassword) {
            confirmPasswordEditText.error = "Passwords do not match"
            return false
        }
        return true
    }

    private fun performSignup(email: String, password: String) {
        val request = SignupRequest(email, password)
        RetrofitClient.apiService.registerUser(request)
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            Toast.makeText(this@Activity_Signup, it.message, Toast.LENGTH_SHORT).show()
                            if (it.success) {
                                startActivity(Intent(this@Activity_Signup, Activity_Login::class.java))
                                finish()
                            }
                        } ?: showToast("Error: Empty response from server")
                    } else {
                        showToast("Error: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.e("Activity_Signup", "Signup failed", t)
                    showToast("Network error: ${t.localizedMessage}")
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}