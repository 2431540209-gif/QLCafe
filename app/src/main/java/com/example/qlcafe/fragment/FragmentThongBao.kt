package com.example.qlcafe.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.adapter.ThongBaoAdapter
import com.example.qlcafe.models.ThongBao
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AlertDialog

class FragmentThongBao : Fragment(R.layout.fragment_notification) {
    private lateinit var adapter: ThongBaoAdapter
    private var listDuLieu = mutableListOf<ThongBao>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvThongBao = view.findViewById<RecyclerView>(R.id.rvThongBao)
        val btnTaoThongBao = view.findViewById<FloatingActionButton>(R.id.btnTaoThongBao)

        val sessionManager = com.example.qlcafe.auth.SessionManager(requireContext())
        val userRole = sessionManager.getUserRole()

        // Chỉ Quản lý mới thấy nút Tạo thông báo Sự Kiện
        if (userRole == "manager") {
            btnTaoThongBao.visibility = View.VISIBLE
            btnTaoThongBao.setOnClickListener {
                hienThiFormTaoThongBao()
            }
        } else {
            btnTaoThongBao.visibility = View.GONE
        }

        // Dữ liệu giả (Đã xóa cái thông báo Nhân sự)
        listDuLieu = mutableListOf(
            ThongBao("KHO", "Kho sắp hết Sữa Tươi", "Chỉ còn 2 hộp trong kho", "Hệ thống tự động phát hiện Sữa tươi tiệt trùng TH True Milk chỉ còn lại 2 hộp. Vui lòng liên hệ nhà cung cấp để nhập thêm.", "10 phút trước"),
            ThongBao("SU_KIEN", "Họp nhân viên cuối tháng", "Vào 8h sáng Chủ Nhật", "Toàn bộ nhân sự Kế toán, Pha chế và Phục vụ có mặt lúc 8h00 sáng Chủ Nhật tại cơ sở 1 để tổng kết doanh thu. Không vắng mặt.", "Hôm qua")
        )

        adapter = ThongBaoAdapter(listDuLieu,
            onItemClick = { thongBaoDuocClick ->
                hienThiChiTiet(thongBaoDuocClick)
            },
            onItemLongClick = { viTri, thongBao ->
                hienThiDialogXoa(viTri, thongBao)
            }
        )

        rvThongBao.layoutManager = LinearLayoutManager(requireContext())
        rvThongBao.adapter = adapter
    }

    private fun hienThiDialogXoa(viTri: Int, thongBao: ThongBao) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa thông báo '${thongBao.title}' không?")
            .setPositiveButton("Xóa") { _, _ ->
                listDuLieu.removeAt(viTri)
                adapter.notifyItemRemoved(viTri)
                Toast.makeText(requireContext(), "Đã xóa thông báo thành công!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun hienThiChiTiet(thongBao: ThongBao) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val viewDialog = layoutInflater.inflate(R.layout.layout_notification_detail, null)
        bottomSheetDialog.setContentView(viewDialog)

        viewDialog.findViewById<TextView>(R.id.tvChiTietTitle).text = thongBao.title
        viewDialog.findViewById<TextView>(R.id.tvChiTietTime).text = thongBao.time
        viewDialog.findViewById<TextView>(R.id.tvChiTietContent).text = thongBao.chiTiet

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

            try {
                // Tự động gán cứng là SU_KIEN luôn, quản lý không cần chọn!
                val thongBaoMoi = ThongBao(
                    loai = "SU_KIEN",
                    title = tieuDe,
                    content = moTa,
                    chiTiet = if (chiTiet.isEmpty()) "Không có chi tiết" else chiTiet,
                    time = "Vừa xong"
                )
                listDuLieu.add(0, thongBaoMoi)
                adapter.notifyItemInserted(0)
                view?.findViewById<RecyclerView>(R.id.rvThongBao)?.scrollToPosition(0)
                Toast.makeText(requireContext(), "Đã phát thông báo sự kiện!", Toast.LENGTH_SHORT).show()
                bottomSheet.dismiss()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        bottomSheet.show()
    }
}