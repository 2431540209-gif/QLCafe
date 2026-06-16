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

// Chú ý: Tui thêm một cái onItemClick (Hàm callback) để bắt sự kiện chạm vào 1 dòng
class ThongBaoAdapter(
    private val dsThongBao: MutableList<ThongBao>,
    private val onItemClick: (ThongBao) -> Unit,
    private val onItemLongClick: (Int, ThongBao)-> Unit
) : RecyclerView.Adapter<ThongBaoAdapter.ThongBaoViewHolder>() {

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
        holder.txtContent.text = tb.content
        holder.txtTime.text = tb.time

        // Trang điểm: Đổi icon và màu sắc tùy theo Loại thông báo
        when (tb.loai) {
            "KHO" -> {
                holder.imgIcon.setImageResource(android.R.drawable.ic_dialog_alert) // Icon cảnh báo
                holder.imgIcon.setBackgroundColor(Color.parseColor("#FFCDD2")) // Nền đỏ nhạt
            }
            "SU_KIEN" -> {
                holder.imgIcon.setImageResource(android.R.drawable.ic_menu_today) // Icon lịch
                holder.imgIcon.setBackgroundColor(Color.parseColor("#BBDEFB")) // Nền xanh nhạt
            }
            "NHAN_SU" -> {
                holder.imgIcon.setImageResource(android.R.drawable.ic_menu_myplaces) // Icon người
                holder.imgIcon.setBackgroundColor(Color.parseColor("#C8E6C9")) // Nền xanh lá nhạt
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