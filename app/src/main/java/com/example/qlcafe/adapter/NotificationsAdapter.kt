package com.example.qlcafe.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.models.ThongBao
import com.example.qlcafe.R

class NotificationsAdapter(
    private val dsThongBao: MutableList<ThongBao>,
    //private val userRole: String, // Thêm quyền
    private val onItemClick: (ThongBao) -> Unit,
    private val onItemLongClick: (Int, ThongBao)-> Unit
) : RecyclerView.Adapter<NotificationsAdapter.ThongBaoViewHolder>() {

    class ThongBaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIcon: ImageView = itemView.findViewById(R.id.imgIcon)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val txtContent: TextView = itemView.findViewById(R.id.txtContent)
        val txtTime: TextView = itemView.findViewById(R.id.txtTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThongBaoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ThongBaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ThongBaoViewHolder, position: Int) {
        val tb = dsThongBao[position]
        holder.txtTitle.text = tb.title
        holder.txtContent.text = tb.short_content
        holder.txtTime.text = tb.created_at

        // Trang điểm: Đổi icon và màu sắc tùy theo Loại thông báo
        when (tb.type) {
            "INVENTORY" -> {
                holder.imgIcon.setImageResource(android.R.drawable.ic_dialog_alert) // Icon cảnh báo
                holder.imgIcon.setBackgroundColor(Color.parseColor("#FFCDD2")) // Nền đỏ nhạt
            }
            "EVENT" -> {
                holder.imgIcon.setImageResource(android.R.drawable.ic_menu_today) // Icon lịch
                holder.imgIcon.setBackgroundColor(Color.parseColor("#BBDEFB")) // Nền xanh nhạt
            }
        }

        // Bắt sự kiện Click: Khi người dùng bấm vào dòng này, nó sẽ ném dữ liệu tb ra ngoài
        holder.itemView.setOnClickListener {
            onItemClick(tb)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(position, tb)
            true
        }
    }

    override fun getItemCount(): Int = dsThongBao.size
}