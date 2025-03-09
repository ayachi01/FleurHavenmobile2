package com.example.fleurhaven.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fleurhaven.R
import com.example.fleurhaven.models.CartItem

class CartAdapter(private val context: Context, private val cartItems: MutableList<CartItem>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]

        // Bind data to the views
        holder.productName.text = cartItem.name
        holder.productPrice.text = "₱ ${cartItem.price}"
        holder.productQuantity.text = cartItem.quantity.toString()

        // Handle quantity increase
        holder.btnIncrease.setOnClickListener {
            cartItem.quantity += 1
            holder.productQuantity.text = cartItem.quantity.toString()
            notifyItemChanged(position) // Refresh the updated item
        }

        // Handle quantity decrease
        holder.btnDecrease.setOnClickListener {
            if (cartItem.quantity > 1) {
                cartItem.quantity -= 1
                holder.productQuantity.text = cartItem.quantity.toString()
                notifyItemChanged(position) // Refresh the updated item
            }
        }

        // Handle remove item from cart
        holder.btnClose.setOnClickListener {
            cartItems.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, cartItems.size) // Refresh the list after removal
            notifyDataSetChanged()  // Notify the adapter for overall change
        }
    }

    override fun getItemCount(): Int = cartItems.size

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.flower_name6)
        val productPrice: TextView = itemView.findViewById(R.id.flower_price6)
        val productQuantity: TextView = itemView.findViewById(R.id.tv_quantity6)
        val productImage: ImageView = itemView.findViewById(R.id.frame6)
        val btnIncrease: Button = itemView.findViewById(R.id.btn_increase6)
        val btnDecrease: Button = itemView.findViewById(R.id.btn_decrease6)
        val btnClose: ImageButton = itemView.findViewById(R.id.btn_close6)
    }
}
