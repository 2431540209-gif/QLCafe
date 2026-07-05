package com.example.qlcafe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.models.NguyenLieu

class NguyenLieuAdapter(private var listNguyenLieu: List<NguyenLieu>) :
    RecyclerView.Adapter<NguyenLieuAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTen: TextView = view.findViewById(R.id.tvTenNguyenLieu)
        val tvSoLuong: TextView = view.findViewById(R.id.tvSoLuong)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nguyen_lieu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listNguyenLieu[position]
        holder.tvTen.text = item.tenNguyenLieu
        holder.tvSoLuong.text = item.soLuong.toString()
    }

    override fun getItemCount() = listNguyenLieu.size

    // Hàm dùng để cập nhật lại danh sách khi có dữ liệu mới
    fun updateData(newList: List<NguyenLieu>) {
        listNguyenLieu = newList
        notifyDataSetChanged()
    }
}