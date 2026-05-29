package com.example.qlcafe

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

// Khai báo Fragment này sẽ dùng giao diện fragment_trang_chu.xml
class FragmentTrangChu : Fragment(R.layout.fragment_trang_chu) {

    // Với Fragment, code logic không viết ở onCreate mà viết ở onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ==========================================
        // 1. ÁNH XẠ (Nhớ thêm chữ "view." ở trước)
        // ==========================================
        val cardDoanhThu = view.findViewById<CardView>(R.id.cardDoanhThu)
        val tvEmployeeName = view.findViewById<TextView>(R.id.tvEmployeeName)
        val tvRoleName = view.findViewById<TextView>(R.id.tvRoleName)

        // ==========================================
        // 2. LẤY DỮ LIỆU TỪ INTENT
        // Fragment không có sẵn intent, phải gọi "requireActivity().intent" để xin từ MainActivity
        // ==========================================
        val tenNhanVien = requireActivity().intent.getStringExtra("NICKNAME") ?: "Khách"
        val chucVuDangNhap = requireActivity().intent.getStringExtra("ROLE") ?: "BARISTA"

        // ==========================================
        // 3. ĐỔ DỮ LIỆU VÀ PHÂN QUYỀN (Giữ nguyên y hệt)
        // ==========================================
        tvEmployeeName.text = tenNhanVien
        tvRoleName.text = chucVuDangNhap

        if (chucVuDangNhap == "QUAN_LY" || chucVuDangNhap == "ADMIN") {
            cardDoanhThu.visibility = View.VISIBLE
        } else {
            cardDoanhThu.visibility = View.GONE
        }

        val btnGoToRevenue = view.findViewById<View>(R.id.btnGoToRevenue)
        btnGoToRevenue.setOnClickListener {
            // Logic chuyển trang
        }
    }
}