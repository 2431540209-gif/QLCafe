package com.example.qlcafe.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.qlcafe.R

class FragmentProfile : Fragment(R.layout.activity_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ánh xạ các thành phần giao diện
        val cardPassword = view.findViewById<CardView>(R.id.cardPassword)
        val cardStaff = view.findViewById<CardView>(R.id.cardStaff)
        val cardInventory = view.findViewById<CardView>(R.id.cardInventory)
        val cardReceipt = view.findViewById<CardView>(R.id.cardReceipt)
        val cardNotification = view.findViewById<CardView>(R.id.cardNotification)
        val cardSetting = view.findViewById<CardView>(R.id.cardSetting)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // Thiết lập sự kiện Click cho từng thẻ chức năng
        cardPassword.setOnClickListener { openFeatureDialog("Đổi mật khẩu") }
        cardStaff.setOnClickListener { openFeatureDialog("Nhân viên") }
        cardInventory.setOnClickListener { openFeatureDialog("Kho nguyên liệu") }
        cardReceipt.setOnClickListener { openFeatureDialog("Hóa đơn") }
        cardNotification.setOnClickListener { openFeatureDialog("Thông báo") }
        cardSetting.setOnClickListener { openFeatureDialog("Cài đặt") }

        // Đăng xuất dùng AlertDialog
        btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đồng ý") { _, _ ->
                    Toast.makeText(context, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }

    private fun openFeatureDialog(type: String) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_thao_tac)

        // Ánh xạ các view trong dialog
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val spinner = dialog.findViewById<Spinner>(R.id.spinner_kho)
        val edt = dialog.findViewById<EditText>(R.id.edt_input)
        val sw = dialog.findViewById<Switch>(R.id.switch_notify)
        val lvNhanVien = dialog.findViewById<ListView>(R.id.lv_nhan_vien)
        val btn = dialog.findViewById<Button>(R.id.btn_xac_nhan)

        tvTitle.text = type

        // 1. Luôn ẩn tất cả trước khi hiện đúng cái cần
        spinner.visibility = View.GONE
        edt.visibility = View.GONE
        sw.visibility = View.GONE
        lvNhanVien.visibility = View.GONE

        // 2. Logic ẩn/hiện và đổ dữ liệu tương ứng theo yêu cầu
        when (type) {
            "Đổi mật khẩu" -> {
                edt.visibility = View.VISIBLE
                edt.hint = "Nhập mật khẩu mới"
            }
            "Kho nguyên liệu" -> {
                spinner.visibility = View.VISIBLE
                lvNhanVien.visibility = View.VISIBLE
                val dataKho = listOf("Cà phê hạt: 10kg", "Sữa tươi: 5L", "Đường: 2kg")
                lvNhanVien.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dataKho)
            }
            "Hóa đơn" -> {
                lvNhanVien.visibility = View.VISIBLE
                val dataHD = listOf(
                    "Hóa đơn #001 - 50.000đ - [Chưa thanh toán]",
                    "Hóa đơn #002 - 120.000đ - [Đã thanh toán]",
                    "Hóa đơn #003 - 85.000đ - [Chưa thanh toán]"
                )
                lvNhanVien.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dataHD)
            }
            "Thông báo" -> {
                sw.visibility = View.VISIBLE
                sw.text = "Bật thông báo ứng dụng"
            }
            "Nhân viên" -> {
                lvNhanVien.visibility = View.VISIBLE
                val dataNV = listOf("Hồng Ngọc Thanh Thanh", "Nguyễn Hoài Phong", "Phạm Nguyễn Hoàng Phúc", "Nguyễn Trọng Tín")
                lvNhanVien.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dataNV)
            }
            "Cài đặt" -> {
                sw.visibility = View.VISIBLE
                sw.text = "Chế độ ban đêm (Dark Mode)"
            }
        }

        btn.setOnClickListener {
            Toast.makeText(context, "Đã xác nhận: $type", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.show()
    }
}