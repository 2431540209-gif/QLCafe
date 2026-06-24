package com.example.qlcafe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.models.ProductOrder

class ProductSelectionAdapter(
    private val products: List<ProductOrder>,
    private val onTotalChanged: () -> Unit
) : RecyclerView.Adapter<ProductSelectionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvProductName)
        val tvPrice: TextView = view.findViewById(R.id.tvProductPrice)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        val tvItemTotal: TextView = view.findViewById(R.id.tvItemTotal)
        val btnMinus: View = view.findViewById(R.id.btnMinus)
        val btnPlus: View = view.findViewById(R.id.btnPlus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.tvName.text = product.name
        holder.tvPrice.text = String.format("%,.0fđ/ly", product.price)
        holder.tvQuantity.text = product.quantity.toString()
        holder.tvItemTotal.text = String.format("%,.0fđ", product.price * product.quantity)

        holder.btnPlus.setOnClickListener {
            product.quantity++
            notifyItemChanged(position)
            onTotalChanged()
        }

        holder.btnMinus.setOnClickListener {
            if (product.quantity > 0) {
                product.quantity--
                notifyItemChanged(position)
                onTotalChanged()
            }
        }
    }

    override fun getItemCount(): Int = products.size
}
