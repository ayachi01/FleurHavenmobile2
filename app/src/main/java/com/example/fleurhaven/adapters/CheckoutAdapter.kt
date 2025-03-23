package com.example.fleurhaven.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fleurhaven.R
import com.example.fleurhaven.models.CartItem

class CheckoutAdapter(private val context: Context, private var cartItems: List<CartItem>) :
    RecyclerView.Adapter<CheckoutAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgCheckout: ImageView = view.findViewById(R.id.img_checkout)
        val tvFlowerName: TextView = view.findViewById(R.id.tv_flower_name)
        val tvFlowerPrice: TextView = view.findViewById(R.id.tv_flower_price)
        val tvFlowerQuantity: TextView = view.findViewById(R.id.tv_flower_quantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.checkout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cartItems[position]

        holder.tvFlowerName.text = item.flower_name
        holder.tvFlowerPrice.text = "₱${"%.2f".format(item.flower_price)}"
        holder.tvFlowerQuantity.text = "Qty: ${item.quantity}"

        Glide.with(context).load(item.flower_image).into(holder.imgCheckout)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    fun updateItems(newItems: List<CartItem>) {
        cartItems = newItems
        notifyDataSetChanged()
    }
}
