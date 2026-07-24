package com.example.qlcafe.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.adapter.NotificationsAdapter
import com.example.qlcafe.database.DatabaseHelper
import com.example.qlcafe.auth.SessionManager
import com.example.qlcafe.models.ThongBao
import com.example.qlcafe.repository.NotificationRepository
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentNotifications : Fragment(R.layout.fragment_notification) {
    private lateinit var adapter: NotificationsAdapter
    private lateinit var sessionManager: SessionManager
    private val repository = NotificationRepository()
    private var listDuLieu = mutableListOf<ThongBao>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvThongBao = view.findViewById<RecyclerView>(R.id.rvThongBao)
        val btnTaoThongBao = view.findViewById<FloatingActionButton>(R.id.btnTaoThongBao)

        sessionManager = SessionManager(requireContext())
        val userRole = sessionManager.getUserRole()

        // Chỉ Quản lý mới thấy nút Tạo thông báo Sự Kiện
        if (userRole == "ADMIN" || userRole == "quan_ly"){
            btnTaoThongBao.visibility = View.VISIBLE
            btnTaoThongBao.setOnClickListener { hienThiFormTaoThongBao() }
        } else {
            btnTaoThongBao.visibility = View.GONE
        }

        adapter = NotificationsAdapter(listDuLieu,
            onItemClick = { thongBaoDuocClick -> hienThiChiTiet(thongBaoDuocClick) },
            onItemLongClick = { viTri, thongBao ->
                if (userRole == "quan_ly"|| userRole == "ADMIN") {
                    // Manager: Hiện Dialog Xóa vĩnh viễn (Server)
                    hienThiDialogXoa(viTri, thongBao)
                } else {
                    // Nhân viên: Hiện Dialog Bỏ qua (Local)
                    xacNhanBoQua(viTri, thongBao)
                }
            }
        )

        rvThongBao.layoutManager = LinearLayoutManager(requireContext())
        rvThongBao.adapter = adapter

        // Load từ SQLite trước để tránh giật lag
        val dbHelper = DatabaseHelper(requireContext())
        val cached = dbHelper.notificationDao.getCachedNotifications()
        val hiddenIds = sessionManager.getHiddenNotifications()
        listDuLieu.clear()
        listDuLieu.addAll(cached.filter { it.id !in hiddenIds })
        adapter.notifyDataSetChanged()
        view.findViewById<View>(R.id.layoutEmpty)?.visibility = if (listDuLieu.isEmpty()) View.VISIBLE else View.GONE

        layDanhSachThongBao()
    }

    private fun layDanhSachThongBao() {
        val dbHelper = DatabaseHelper(requireContext())
        repository.getNotifications { success, data, message ->
            if (success && data != null) {
                dbHelper.notificationDao.cacheNotifications(data)
                val hiddenIds = sessionManager.getHiddenNotifications()

                // Lọc bỏ những thông báo có ID nằm trong danh sách đã xóa
                listDuLieu.clear()
                listDuLieu.addAll(data.filter { it.id !in hiddenIds })

                adapter.notifyDataSetChanged()
                view?.findViewById<View>(R.id.layoutEmpty)?.visibility = if (listDuLieu.isEmpty()) View.VISIBLE else View.GONE
            } else {
                if (listDuLieu.isEmpty()) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun hienThiDialogXoa(viTri: Int, thongBao: ThongBao) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa thông báo")
            .setMessage("Bạn có chắc chắn muốn xóa vĩnh viễn thông báo này khỏi hệ thống?")
            .setPositiveButton("Xóa") { _, _ ->
                repository.deleteNotification(thongBao.id) { success, message ->
                    if (success) {
                        listDuLieu.removeAt(viTri)
                        adapter.notifyItemRemoved(viTri)
                        Toast.makeText(requireContext(), "Đã xóa thông báo!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Lỗi khi xóa: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun xacNhanBoQua(viTri: Int, thongBao: ThongBao) {
        AlertDialog.Builder(requireContext())
            .setTitle("Bỏ qua thông báo")
            .setMessage("Bạn không muốn thấy thông báo này nữa?")
            .setPositiveButton("Bỏ qua") { _, _ ->
                // 1. Lưu ID vào máy người dùng
                sessionManager.addHiddenNotification(thongBao.id)

                // 2. Xóa khỏi danh sách hiện tại để cập nhật giao diện ngay
                listDuLieu.removeAt(viTri)
                adapter.notifyItemRemoved(viTri)

                Toast.makeText(requireContext(), "Đã ẩn thông báo này!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun hienThiChiTiet(thongBao: ThongBao) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val viewDialog = layoutInflater.inflate(R.layout.layout_notification_detail, null)
        bottomSheetDialog.setContentView(viewDialog)

        viewDialog.findViewById<TextView>(R.id.tvChiTietTitle).text = thongBao.title
        viewDialog.findViewById<TextView>(R.id.tvChiTietTime).text = thongBao.created_at
        viewDialog.findViewById<TextView>(R.id.tvChiTietContent).text = thongBao.details ?: thongBao.short_content

        viewDialog.findViewById<Button>(R.id.btnDong).setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun hienThiFormTaoThongBao() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val viewForm = layoutInflater.inflate(R.layout.layout_create_notification, null)
        bottomSheet.setContentView(viewForm)

        val edtTieuDe = viewForm.findViewById<EditText>(R.id.edtTieuDe)
        val edtMoTa = viewForm.findViewById<EditText>(R.id.edtMoTa)
        val edtChiTiet = viewForm.findViewById<EditText>(R.id.edtChiTiet)
        val btnGui = viewForm.findViewById<Button>(R.id.btnGuiThongBao)

        btnGui.setOnClickListener {
            val tieuDe = edtTieuDe.text.toString().trim()
            val moTa = edtMoTa.text.toString().trim()
            val chiTiet = edtChiTiet.text.toString().trim()

            if (tieuDe.isEmpty() || moTa.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đủ Tiêu đề và Mô tả!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnGui.isEnabled = false
            btnGui.text = "Đang gửi..."

            val thongBaoMoi = ThongBao(type = "EVENT", title = tieuDe, short_content = moTa, details = chiTiet)
            repository.addNotification(thongBaoMoi) { success, message ->
                if (success) {
                    Toast.makeText(requireContext(), "Đã phát thông báo sự kiện!", Toast.LENGTH_SHORT).show()
                    bottomSheet.dismiss()
                    layDanhSachThongBao() // Tải lại danh sách mới
                } else {
                    btnGui.isEnabled = true
                    btnGui.text = "Gửi Thông Báo"
                    Toast.makeText(requireContext(), "Lỗi: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
        bottomSheet.show()
    }
}
