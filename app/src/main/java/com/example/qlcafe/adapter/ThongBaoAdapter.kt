package com.example.qlcafe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.models.ThongBao

// Lớp Adapter nhận vào một danh sách thông báo
class ThongBaoAdapter(private val dsThongBao: MutableList<ThongBao>) :
    RecyclerView.Adapter<ThongBaoAdapter.ThongBaoViewHolder>() {

    // 1. Lớp ViewHolder (Đại diện cho cái Bao bì item_thong_bao.xml)
    // Nó giúp tìm sẵn các thành phần giao diện để lát nữa nhét chữ vào cho lẹ
    class ThongBaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIcon: ImageView = itemView.findViewById(R.id.imgIcon)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val txtContent: TextView = itemView.findViewById(R.id.txtContent)
        val txtTime: TextView = itemView.findViewById(R.id.txtTime)
    }

    // 2. Nhiệm vụ 1 của Adapter: Tạo ra các Bao bì trống
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThongBaoViewHolder {
        // Bơm cái giao diện item_thong_bao.xml lên
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_thong_bao, parent, false)
        return ThongBaoViewHolder(view)
    }

    // 3. Nhiệm vụ 2 của Adapter: Lấy dữ liệu nhét vào Bao bì
    override fun onBindViewHolder(holder: ThongBaoViewHolder, position: Int) {
        val thongBaoHienTai = dsThongBao[position] // Lấy thông báo ở vị trí tương ứng

        // Đổ dữ liệu vào các thẻ TextView, ImageView
        holder.imgIcon.setImageResource(thongBaoHienTai.iconId)
        holder.txtTitle.text = thongBaoHienTai.title
        holder.txtContent.text = thongBaoHienTai.content
        holder.txtTime.text = thongBaoHienTai.time
    }

    // 4. Nhiệm vụ 3 của Adapter: Báo cáo xem trong kho có tổng cộng bao nhiêu món hàng
    override fun getItemCount(): Int {
        return dsThongBao.size
    }
}