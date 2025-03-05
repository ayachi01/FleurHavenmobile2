package com.example.fleurhaven

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fleurhaven.api.RetrofitClient
import com.example.fleurhaven.models.ApiResponse
import com.example.fleurhaven.models.UpdateAddressRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Activity_Profile : AppCompatActivity() {

    private lateinit var addressTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = getSharedPreferences("User_Profile", Context.MODE_PRIVATE)

        val profilePref = getSharedPreferences("user_data", MODE_PRIVATE)
        val userEmail = profilePref.getString("email", null)
        val cartIcon = findViewById<ImageButton>(R.id.cart_icon)
        val editAddress = findViewById<ImageButton>(R.id.editaddress_icon)
        addressTextView = findViewById(R.id.addresstxt)

        if (userEmail == null) {
            startActivity(Intent(this, Activity_Login::class.java))
            finish()
            return
        }

        findViewById<TextView>(R.id.emailtxt).text = userEmail
        loadAddress()

        cartIcon.setOnClickListener {
            val savedAddress = sharedPreferences.getString("address", null)
            if (savedAddress == null) {
                Toast.makeText(this, "Please set your address before proceeding to the cart.", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, Activity_Cart::class.java))
            }
        }

        editAddress.setOnClickListener {
            showSetAddressDialog()
        }
    }

    private fun showSetAddressDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Set Your Address")

        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.activity_set_address, null)
        builder.setView(dialogView)

        val addressEditText = dialogView.findViewById<EditText>(R.id.addressEditText)
        val saveAddressButton = dialogView.findViewById<Button>(R.id.saveAddressButton)

        val dialog = builder.create()

        saveAddressButton.setOnClickListener {
            val address = addressEditText.text.toString().trim()
            if (address.isNotEmpty()) {
                saveAddress(address, dialog)
            } else {
                addressEditText.error = "Address cannot be empty"
            }
        }

        dialog.show()
    }

    private fun saveAddress(address: String, dialog: AlertDialog) {
        val profilePref = getSharedPreferences("user_data", MODE_PRIVATE)
        val userEmail = profilePref.getString("email", null)

        if (userEmail == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBody = UpdateAddressRequest(userEmail, address)

        // Debugging
        Log.d("DEBUG", "Sending Update Request: Email=${requestBody.email}, Address=${requestBody.address}")

        RetrofitClient.instance.updateAddress(requestBody)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    Log.d("DEBUG", "Response Code: ${response.code()}, Body: ${response.body()}")

                    if (response.isSuccessful && response.body() != null) {
                        val apiResponse = response.body()
                        if (apiResponse!!.success) {
                            sharedPreferences.edit().putString("address", address).apply()
                            addressTextView.text = address
                            Toast.makeText(this@Activity_Profile, "Address updated!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(this@Activity_Profile, apiResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@Activity_Profile, "Server error. Try again!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.e("DEBUG", "Request failed: ${t.message}")
                    Toast.makeText(this@Activity_Profile, "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadAddress() {
        val savedAddress = sharedPreferences.getString("address", null)
        if (savedAddress != null) {
            addressTextView.text = savedAddress
        }
    }
}
