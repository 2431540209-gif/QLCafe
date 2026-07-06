package com.example.qlcafe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.models.UserInfo

class StaffAdapter(
    private val staffList: List<UserInfo>,
    private val clickListener: (UserInfo) -> Unit,
    private val longClickListener: (UserInfo) -> Unit
) : RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {

    class StaffViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Ánh xạ chuẩn xác với các ID trong file item_staff.xml
        val tvName: TextView = view.findViewById(R.id.txtUsername)
        val tvRolePermission: TextView = view.findViewById(R.id.txtRole)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_staff, parent, false)
        return StaffViewHolder(view)
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        val staff = staffList[position]

        // Ưu tiên hiển thị tên nhân viên, nếu không có mới hiển thị số điện thoại
        holder.tvName.text = staff.username ?: staff.phone ?: "Chưa cập nhật tên"

        // Format chức vụ thân thiện
        val displayRole = when (staff.role?.lowercase()?.trim()) {
            "admin" -> "Quản trị viên"
            "quan_ly" -> "Quản lý cửa hàng"
            "nhan_vien_order" -> "Nhân viên Order"
            "nhan_vien_pha_che" -> "Nhân viên pha chế"
            null, "" -> "Chưa phân chức vụ"
            else -> staff.role.uppercase()
        }

        // Format đặc quyền thân thiện
        val displayPermission = when {
            staff.dacQuyen.isNullOrEmpty() -> "Không có tác vụ riêng"
            staff.dacQuyen.contains("tao_don_hang") -> "Tác vụ: Tạo đơn"
            staff.dacQuyen.contains("ql_san_pham") -> "Tác vụ: QL Sản phẩm"
            staff.dacQuyen.contains("ql_kho") -> "Tác vụ: QL Kho"
            else -> "Tác vụ: ${staff.dacQuyen}"
        }

        // Gộp chung vào textView txtRole bên dưới tên
        holder.tvRolePermission.text = "$displayRole | $displayPermission"

        // Xử lý chạm bình thường (Click)
        holder.itemView.setOnClickListener {
            clickListener(staff)
        }

        // Xử lý chạm giữ lâu (Long Click) để mở hộp thoại xóa
        holder.itemView.setOnLongClickListener {
            longClickListener(staff)
            true // Bắt buộc trả về true để hệ thống ghi nhận đã xử lý xong sự kiện Long Click
        }
    }

    override fun getItemCount(): Int = staffList.size
}