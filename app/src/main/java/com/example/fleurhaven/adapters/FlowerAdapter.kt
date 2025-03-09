package com.example.fleurhaven.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fleurhaven.Activity_Cart
import com.example.fleurhaven.Activity_Main
import com.example.fleurhaven.R
import com.example.fleurhaven.models.Flower

class FlowerAdapter(private val context: Context, private var flowerList: MutableList<Flower>) :
    RecyclerView.Adapter<FlowerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.flowerImage)
        val name: TextView = view.findViewById(R.id.flowerName)
        val price: TextView = view.findViewById(R.id.flowerPrice)
        val addToCart: Button = view.findViewById(R.id.addToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.flower_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val flower = flowerList[position]
        holder.name.text = flower.name
        holder.price.text = "₱${flower.price}"

        // Load image using Glide with placeholder
        Glide.with(context)
            .load(flower.image_url.ifEmpty { "https://via.placeholder.com/150" }) // ✅ Default image
            .into(holder.image)

        // Handle Add to Cart button click
        holder.addToCart.setOnClickListener {
            addToCart(flower)
        }
    }

    override fun getItemCount() = flowerList.size

    // Function to update the list dynamically
    fun updateFlowers(newFlowers: List<Flower>) {
        flowerList.clear()
        flowerList.addAll(newFlowers)
        notifyDataSetChanged()
    }

    // Function to handle adding flowers to cart
    private fun addToCart(flower: Flower) {
        // Retrieve cart data from SharedPreferences
        val sharedPreferences = context.getSharedPreferences("cart_data", Context.MODE_PRIVATE)
        val cartItemsString = sharedPreferences.getString("cart_items", "")
        val cartItemsList = cartItemsString?.split(",")?.toMutableList() ?: mutableListOf()

        // Add the flower to the cart list
        cartItemsList.add("${flower.name}:${flower.price}:${flower.image_url}")

        // Save the updated cart items list to SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("cart_items", cartItemsList.joinToString(","))
        editor.apply()

        // Show a Toast message confirming the addition
        Toast.makeText(context, "${flower.name} added to cart!", Toast.LENGTH_SHORT).show()

        // Optionally, update the cart count in the Activity_Main
        if (context is Activity_Main) {
            context.updateCartCount()
        }
    }

}
