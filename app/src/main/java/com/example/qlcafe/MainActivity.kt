package com.example.qlcafe // Nhớ giữ nguyên package của bạn

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // ==========================================
        // BƯỚC 1: ÁNH XẠ (Kết nối Kotlin với XML)
        // ==========================================
        val cardDoanhThu = findViewById<CardView>(R.id.cardDoanhThu)

        // Nhớ đảm bảo trong file activity_dashboard.xml bạn đã đặt id cho 2 chữ này nhé
        val tvEmployeeName = findViewById<TextView>(R.id.tvEmployeeName)
        val tvRoleName = findViewById<TextView>(R.id.tvRoleName)

        // ==========================================
        // BƯỚC 2: "CHỤP" DỮ LIỆU TỪ TRANG ĐĂNG NHẬP GỬI QUA
        // ==========================================
        // Lấy tên và chức vụ từ Intent. Nếu vì lý do nào đó bị lỗi không lấy được, nó sẽ mặc định là "Khách" và "BARISTA"
        val tenNhanVien = intent.getStringExtra("NICKNAME") ?: "Khách"
        val chucVuDangNhap = intent.getStringExtra("ROLE") ?: "BARISTA"

        // ==========================================
        // BƯỚC 3: ĐỔ DỮ LIỆU LÊN GIAO DIỆN VÀ PHÂN QUYỀN
        // ==========================================
        // 1. Đổi tên và chức vụ trên Header
        tvEmployeeName.text = tenNhanVien
        tvRoleName.text = chucVuDangNhap

        // 2. Phân quyền ẩn/hiện thẻ doanh thu
        if (chucVuDangNhap == "QUAN_LY" || chucVuDangNhap == "ADMIN") {
            cardDoanhThu.visibility = View.VISIBLE
        } else {
            cardDoanhThu.visibility = View.GONE
        }

        // Sự kiện bấm nút (Tạm thời để trống như bạn đang làm)
        val btnGoToRevenue = findViewById<View>(R.id.btnGoToRevenue)
        btnGoToRevenue.setOnClickListener {
            // Viết code chuyển trang qua màn hình Doanh thu chi tiết ở đây
        }
    }
}