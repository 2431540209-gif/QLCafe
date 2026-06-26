package com.example.qlcafe.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.adapter.ThongBaoAdapter
import com.example.qlcafe.models.ThongBao
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
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

        if (userRole == "manager") {
            btnTaoThongBao.visibility = View.VISIBLE
            btnTaoThongBao.setOnClickListener {
                hienThiFormTaoThongBao()
            }
        } else {
            btnTaoThongBao.visibility = View.GONE
        }
        // Tạo dữ liệu giả định chuẩn mô hình mới
        listDuLieu = mutableListOf(
            ThongBao("KHO", "Kho sắp hết Sữa Tươi", "Chỉ còn 2 hộp trong kho", "Hệ thống tự động quét kho lúc 14:00 ngày hôm nay phát hiện Sữa tươi tiệt trùng TH True Milk chỉ còn lại 2 hộp. Vui lòng liên hệ nhà cung cấp để nhập thêm gấp cho ca tối.", "10 phút trước"),
            ThongBao("SU_KIEN", "Họp nhân viên cuối tháng", "Vào 8h sáng Chủ Nhật", "Toàn bộ nhân sự Kế toán, Pha chế và Phục vụ có mặt lúc 8h00 sáng Chủ Nhật tại cơ sở 1 để tổng kết doanh thu và trao thưởng nhân viên xuất sắc. Không vắng mặt với mọi lý do.", "Hôm qua"),
            ThongBao("NHAN_SU", "Đổi ca thành công", "Ca sáng thứ 6", "Quản lý đã phê duyệt yêu cầu đổi ca của bạn. Bạn sẽ nghỉ ca Sáng thứ 6 và làm bù ca Tối thứ 7 thay cho nhân viên Tuấn Khoa.", "2 ngày trước")
        )

        // Khởi tạo Adapter và Lắng nghe sự kiện Click
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

                // TODO: (Sau này) Gọi lệnh xóa khỏi Database (API MySQL hoặc SQLite) ở đây
                // Ví dụ: int res = database.deleteThongBao(thongBao.id)
                // if (res > 0) { ... }
                listDuLieu.removeAt(viTri)
                adapter.notifyItemRemoved(viTri)
                Toast.makeText(requireContext(), "Đã xóa thông báo thành công!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null) // Bấm hủy thì bảng tự đóng, không làm gì cả
            .show()
    }
    // HÀM MỞ BẢNG TRƯỢT TỪ DƯỚI LÊN
    private fun hienThiChiTiet(thongBao: ThongBao) {
        // 1. Tạo hộp thoại trượt
        val bottomSheetDialog = BottomSheetDialog(requireContext())

        // 2. Bơm cái giao diện bạn vừa thiết kế vào hộp thoại
        val viewDialog = layoutInflater.inflate(R.layout.layout_notification_detail, null)
        bottomSheetDialog.setContentView(viewDialog)

        // 3. Đổ dữ liệu từ item được click vào cái bảng trượt
        viewDialog.findViewById<TextView>(R.id.tvChiTietTitle).text = thongBao.title
        viewDialog.findViewById<TextView>(R.id.tvChiTietTime).text = thongBao.time
        viewDialog.findViewById<TextView>(R.id.tvChiTietContent).text = thongBao.chiTiet

        // 4. Gắn nút "Đã hiểu" để tắt bảng trượt
        viewDialog.findViewById<Button>(R.id.btnDong).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        // 5. Chính thức cho xuất hiện!
        bottomSheetDialog.show()
    }
    private fun hienThiFormTaoThongBao() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val viewForm = layoutInflater.inflate(R.layout.layout_create_notification, null)
        bottomSheet.setContentView(viewForm)

        // Setup cái Menu xổ xuống (Spinner) cho phân loại
        val spnLoai = viewForm.findViewById<Spinner>(R.id.spnLoaiThongBao)
        val danhSachLoai = arrayOf("Sự Kiện", "Kho Bãi", "Nhân Sự")
        spnLoai.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            danhSachLoai
        )

        val edtTieuDe = viewForm.findViewById<EditText>(R.id.edtTieuDe)
        val edtMoTa = viewForm.findViewById<EditText>(R.id.edtMoTa)
        val edtChiTiet = viewForm.findViewById<EditText>(R.id.edtChiTiet)
        val btnGui = viewForm.findViewById<Button>(R.id.btnGuiThongBao)
        val layoutNguoiNhan = viewForm.findViewById<LinearLayout>(R.id.layoutNguoiNhan)
        val spnNguoiNhan = viewForm.findViewById<Spinner>(R.id.spnNguoiNhan)

        // 1. Tạo dữ liệu giả cho danh sách nhân viên (Sau này lấy từ MySQL xuống)
        val danhSachNhanVien = arrayOf("Tất cả mọi người", "Tuấn Khoa (Pha chế)", "Du Dĩnh (Phục vụ)", "Mỹ Lê (Quản lý)")
        spnNguoiNhan.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, danhSachNhanVien)

        spnLoai.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val loaiDangChon = danhSachLoai[position]

                if (loaiDangChon == "Nhân Sự") {
                    // Nếu chọn Nhân Sự -> Bật form chọn người lên
                    layoutNguoiNhan.visibility = View.VISIBLE
                } else {
                    // Khác Nhân Sự -> Giấu đi và reset lựa chọn về mặc định
                    layoutNguoiNhan.visibility = View.GONE
                    spnNguoiNhan.setSelection(0) // 0 là "Tất cả mọi người"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        btnGui.setOnClickListener {
            val nguoiNhanDuocChon = spnNguoiNhan.selectedItem.toString()
            var nguoi_nhan_id: Int? = null // Mặc định là null (Gửi tất cả)

            if (layoutNguoiNhan.visibility == View.VISIBLE && nguoiNhanDuocChon != "Tất cả mọi người") {
                // Tùy theo tên được chọn mà gán ID thực tế trong database (Ví dụ giả định)
                when (nguoiNhanDuocChon) {
                    "Tuấn Khoa (Pha chế)" -> nguoi_nhan_id = 3
                    "Du Dĩnh (Phục vụ)" -> nguoi_nhan_id = 5
                    "Mỹ Lê (Quản lý)" -> nguoi_nhan_id = 8
                }
            }
            // 1. Chặn lỗi rỗng Spinner
            if (spnLoai.selectedItem == null) {
                Toast.makeText(requireContext(), "Lỗi: Chưa chọn loại thông báo!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tieuDe = edtTieuDe.text.toString().trim()
            val moTa = edtMoTa.text.toString().trim()
            val chiTiet = edtChiTiet.text.toString().trim()

            if (tieuDe.isEmpty() || moTa.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đủ Tiêu đề và Mô tả!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                val loaiDuocChọn = when(spnLoai.selectedItem.toString()) {
                    "Kho Bãi" -> "KHO"
                    "Nhân Sự" -> "NHAN_SU"
                    else -> "SU_KIEN"
                }
                val thongBaoMoi = ThongBao(
                    loai = loaiDuocChọn,
                    title = tieuDe,
                    content = moTa,
                    chiTiet = if (chiTiet.isEmpty()) "Không có chi tiết" else chiTiet,
                    time = "Vừa xong"
                )
                listDuLieu.add(0, thongBaoMoi)
                adapter.notifyItemInserted(0)
                view?.findViewById<RecyclerView>(R.id.rvThongBao)?.scrollToPosition(0)
                Toast.makeText(requireContext(), "Tạo thông báo thành công!", Toast.LENGTH_SHORT).show()
                bottomSheet.dismiss()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }

        bottomSheet.show()
    }
}