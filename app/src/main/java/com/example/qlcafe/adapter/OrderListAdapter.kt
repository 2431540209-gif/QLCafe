package com.example.qlcafe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.models.Order
import com.example.qlcafe.models.OrderStatus
import java.util.*

class OrderListAdapter(
    private val orders: List<Order>,
    private val onProcessClick: (Order) -> Unit,
    private val onCancelClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvId: TextView = view.findViewById(R.id.tvOrderId)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvCustomer: TextView = view.findViewById(R.id.tvCustomer)
        val tvItems: TextView = view.findViewById(R.id.tvItems)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val btnProcess: TextView = view.findViewById(R.id.btnProcessOrder)
        val btnCancel: TextView = view.findViewById(R.id.btnCancelOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_dark, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.tvId.text = order.id
        holder.tvCustomer.text = "${order.customerName} — ${order.table}"
        holder.tvItems.text = order.items
        holder.tvPrice.text = String.format(Locale.getDefault(), "%,.0fđ", order.price)
        holder.tvTime.text = order.time

        when (order.status) {
            OrderStatus.PENDING -> {
                holder.tvStatus.text = "Chờ xử lý"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_pending)
                holder.tvStatus.setTextColor(holder.itemView.context.getColor(R.color.status_pending))
                holder.btnProcess.visibility = View.VISIBLE
                holder.btnCancel.visibility = View.VISIBLE
            }
            OrderStatus.PROCESSED -> {
                holder.tvStatus.text = "Đã xử lý"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_completed)
                holder.tvStatus.setTextColor(holder.itemView.context.getColor(R.color.status_completed))
                holder.btnProcess.visibility = View.GONE
                holder.btnCancel.visibility = View.GONE
            }
            OrderStatus.CANCELLED -> {
                holder.tvStatus.text = "Đã hủy"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_cancelled)
                holder.tvStatus.setTextColor(holder.itemView.context.getColor(R.color.status_cancelled))
                holder.btnProcess.visibility = View.GONE
                holder.btnCancel.visibility = View.GONE
            }
        }

        holder.btnProcess.setOnClickListener {
            onProcessClick(order)
        }
        
        holder.btnCancel.setOnClickListener {
            onCancelClick(order)
        }
    }

    override fun getItemCount(): Int = orders.size
}
