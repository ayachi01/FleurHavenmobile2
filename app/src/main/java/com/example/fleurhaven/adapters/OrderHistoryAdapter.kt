package com.example.fleurhaven.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fleurhaven.R
import com.example.fleurhaven.api.ApiService
import com.example.fleurhaven.api.RetrofitClient
import com.example.fleurhaven.models.ApiResponse
import com.example.fleurhaven.models.Order
import com.example.fleurhaven.models.OrderStatusUpdateRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryAdapter(
    private val context: Context,
    private var orderList: MutableList<Order>,
    private val onCancelClick: (Order) -> Unit,
    private val onReceivedClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val flowerImage: ImageView = view.findViewById(R.id.frame1)
        val flowerName: TextView = view.findViewById(R.id.flower_name)
        val flowerPrice: TextView = view.findViewById(R.id.flower_price)
        val quantity: TextView = view.findViewById(R.id.tv_item)
        val cancelButton: Button = view.findViewById(R.id.cancel_button)
        val receivedButton: Button = view.findViewById(R.id.received_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.orderhistory_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orderList[position]
        holder.flowerName.text = order.flowerName ?: "Unknown"
        holder.flowerPrice.text = "₱${"%.2f".format(order.flowerPrice ?: 0.0)}"
        holder.quantity.text = "Item: ${order.quantity ?: 0}"

        val imageUrl = if (!order.image_url.isNullOrEmpty() && order.image_url.startsWith("http")) {
            order.image_url
        } else {
            "https://via.placeholder.com/150"
        }
        Log.d("OrderHistoryAdapter", "Loading image URL: $imageUrl")

        Glide.with(context)
            .load(imageUrl)
            .into(holder.flowerImage)

        holder.cancelButton.visibility = if (order.status == "Processing") View.VISIBLE else View.GONE
        holder.receivedButton.visibility = if (order.status == "Delivered") View.VISIBLE else View.GONE

        holder.cancelButton.setOnClickListener {
            val orderId = order.id
            val updateRequest = OrderStatusUpdateRequest(orderId, "Cancelled") // ✅ Use correct request format
            val apiService = RetrofitClient.apiService

            val call = apiService.updateOrderStatus(updateRequest)
            call.enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        order.status = "Cancelled"
                        orderList.remove(order) // ✅ Remove order from list
                        notifyDataSetChanged() // ✅ Refresh RecyclerView
                    } else {
                        Log.e("OrderHistoryAdapter", "Failed to update status: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.e("OrderHistoryAdapter", "Failed to update order: ${t.message}")
                }
            })
        }



        holder.receivedButton.setOnClickListener { onReceivedClick(order) }
    }

    override fun getItemCount(): Int = orderList.size

    fun updateOrders(newOrders: List<Order>) {
        Log.d("OrderHistoryAdapter", "Updating orders. New size: ${newOrders.size}")
        orderList.clear()
        orderList.addAll(newOrders)
        notifyDataSetChanged()
    }
}
