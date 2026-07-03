package com.example.qlcafe.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.models.AttendanceRequest
import com.google.android.material.button.MaterialButton

class AttendanceRequestAdapter(
    private var dsDon: List<AttendanceRequest>,
    private val userRole: String, // Thêm biến nhận diện Quản lý hay Nhân viên
    private val onActionClick: (Int, String) -> Unit, // Truyền sự kiện bấm nút Duyệt/Từ chối
    private val onEditClick: (AttendanceRequest) -> Unit, // Sự kiện sửa lịch
    private val onDeleteClick: (Int) -> Unit // Sự kiện xóa lịch
) : RecyclerView.Adapter<AttendanceRequestAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvItemDate)
        val tvStatus: TextView = itemView.findViewById(R.id.tvItemStatus)
        val tvShift: TextView = itemView.findViewById(R.id.tvItemShift)
        val tvTime: TextView = itemView.findViewById(R.id.tvItemTime)

        // Khai báo 2 nút Duyệt/Từ chối cho quản lý
        val layoutActions: LinearLayout = itemView.findViewById(R.id.layoutManagerActions)
        val btnDuyet: MaterialButton = itemView.findViewById(R.id.btnDuyet)
        val btnTuChoi: MaterialButton = itemView.findViewById(R.id.btnTuChoi)

        // Khai báo 2 nút Sửa/Xóa cho nhân viên
        val layoutStaffActions: LinearLayout = itemView.findViewById(R.id.layoutStaffActions)
        val btnSua: MaterialButton = itemView.findViewById(R.id.btnSua)
        val btnXoa: MaterialButton = itemView.findViewById(R.id.btnXoa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_attendance_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val don = dsDon[position]

        val parts = don.request_date.split("-")
        holder.tvDate.text = if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else don.request_date
        holder.tvShift.text = "${don.branch} - ${don.shift_name}"
        val startTime = don.start_time.take(5)
        val endTime = don.end_time.take(5)
        holder.tvTime.text = "$startTime - $endTime"

        // LOGIC HIỆN NÚT DUYỆT: Nếu là Manager VÀ đơn đang "pending" thì mới hiện 2 nút
        if (userRole == "manager" && don.status == "pending") {
            holder.layoutActions.visibility = View.VISIBLE
        } else {
            holder.layoutActions.visibility = View.GONE
        }

        // LOGIC HIỆN NÚT SỬA/XÓA: Nếu đơn đang "pending" thì cho phép Sửa/Xóa
        if (don.status == "pending") {
            holder.layoutStaffActions.visibility = View.VISIBLE
        } else {
            holder.layoutStaffActions.visibility = View.GONE
        }

        // Bắt sự kiện bấm nút Duyệt/Từ chối
        holder.btnDuyet.setOnClickListener { onActionClick(don.id, "approved") }
        holder.btnTuChoi.setOnClickListener { onActionClick(don.id, "rejected") }

        // Bắt sự kiện bấm nút Sửa/Xóa
        holder.btnSua.setOnClickListener { onEditClick(don) }
        holder.btnXoa.setOnClickListener { onDeleteClick(don.id) }

        when (don.status) {
            "pending" -> {
                holder.tvStatus.text = "Chưa duyệt"
                holder.tvStatus.setTextColor(Color.parseColor("#856404"))
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FFF3CD"))
            }
            "approved" -> {
                holder.tvStatus.text = "Đã duyệt"
                holder.tvStatus.setTextColor(Color.parseColor("#155724"))
                holder.tvStatus.setBackgroundColor(Color.parseColor("#D4EDDA"))
            }
            "rejected" -> {
                holder.tvStatus.text = "Từ chối"
                holder.tvStatus.setTextColor(Color.parseColor("#721C24"))
                holder.tvStatus.setBackgroundColor(Color.parseColor("#F8D7DA"))
            }
        }
    }

    override fun getItemCount(): Int = dsDon.size

    fun updateData(newList: List<AttendanceRequest>) {
        dsDon = newList
        notifyDataSetChanged()
    }
}