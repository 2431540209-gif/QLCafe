package com.example.qlcafe.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.qlcafe.R
import com.example.qlcafe.activity.MainActivity
import com.example.qlcafe.activity.QLKhoActivity
import com.example.qlcafe.activity.StaffActivity
import com.example.qlcafe.activity.QuanLyDonHangActivity
import com.example.qlcafe.auth.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class FragmentProfile : Fragment(R.layout.activity_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ánh xạ các thành phần giao diện
        val cardPassword = view.findViewById<CardView>(R.id.cardPassword)
        val cardInventory = view.findViewById<CardView>(R.id.cardInventory)
        val cardReceipt = view.findViewById<CardView>(R.id.cardReceipt)
        val cardNotification = view.findViewById<CardView>(R.id.cardNotification)
        val cardSetting = view.findViewById<CardView>(R.id.cardSetting)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // Thiết lập sự kiện Click cho từng thẻ chức năng
        cardPassword.setOnClickListener { openChangePasswordDialog() }
        
        cardInventory.setOnClickListener { 
            val intent = Intent(requireContext(), QLKhoActivity::class.java)
            val sessionManager = SessionManager(requireContext())
            intent.putExtra("ROLE", sessionManager.getUserRole())
            startActivity(intent)
        }
        
        cardReceipt.setOnClickListener { 
            val intent = Intent(requireContext(), QuanLyDonHangActivity::class.java)
            startActivity(intent)
        }
        
        cardNotification.setOnClickListener { 
            // Chuyển sang Tab Thông báo trên Bottom Navigation của MainActivity
            (requireActivity() as? MainActivity)?.let { mainActivity ->
                val bottomNav = mainActivity.findViewById<BottomNavigationView>(R.id.bottomNav)
                bottomNav.selectedItemId = R.id.nav_notifications
            }
        }
        
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
                
                // Đọc trạng thái Dark Mode hiện tại của ứng dụng
                val currentMode = androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode()
                sw.isChecked = (currentMode == androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES)
                
                sw.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
                        )
                    } else {
                        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
                        )
                    }
                }
            }
        }

        btn.setOnClickListener {
            Toast.makeText(context, "Đã xác nhận: $type", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun openChangePasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        
        // Custom Title cho đẹp mắt
        val tvTitle = TextView(requireContext()).apply {
            text = "Đổi Mật Khẩu"
            textSize = 20f
            setTextColor(Color.parseColor("#4A2C11")) // Nâu tông quán
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, dpToPx(24f), 0, dpToPx(10f))
        }
        builder.setCustomTitle(tvTitle)

        // Khung Layout nhập liệu
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(24f), dpToPx(10f), dpToPx(24f), dpToPx(20f))
        }

        // Hàm tiện ích tạo ô nhập mật khẩu đẹp mắt, bo tròn góc
        fun createPasswordInput(hintText: String): EditText {
            return EditText(requireContext()).apply {
                hint = hintText
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                textSize = 15f
                setPadding(dpToPx(16f), dpToPx(14f), dpToPx(16f), dpToPx(14f))
                background = android.graphics.drawable.GradientDrawable().apply {
                    setColor(Color.parseColor("#F5F5F5"))
                    cornerRadius = dpToPx(12f).toFloat()
                    setStroke(dpToPx(1f), Color.parseColor("#E0E0E0"))
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dpToPx(14f)
                }
            }
        }

        val edtCurrentPass = createPasswordInput("Mật khẩu hiện tại")
        val edtNewPass = createPasswordInput("Mật khẩu mới")
        val edtConfirmPass = createPasswordInput("Xác nhận mật khẩu mới")

        container.addView(edtCurrentPass)
        container.addView(edtNewPass)
        container.addView(edtConfirmPass)
        builder.setView(container)

        // Các nút hành động
        builder.setPositiveButton("Cập nhật") { dialog, _ ->
            val current = edtCurrentPass.text.toString().trim()
            val new = edtNewPass.text.toString().trim()
            val confirm = edtConfirmPass.text.toString().trim()

            if (current.isEmpty() || new.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin mật khẩu!", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            if (new != confirm) {
                Toast.makeText(requireContext(), "Xác nhận mật khẩu mới không trùng khớp!", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            // Cập nhật lại mật khẩu giả lập trong FakeDatabase
            val sharedPreferences = requireActivity().getSharedPreferences("FakeDatabase", Context.MODE_PRIVATE)
            sharedPreferences.edit().apply {
                putString("saved_password", new)
                apply()
            }

            Toast.makeText(requireContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }
        
        val dialog = builder.create()
        dialog.show()

        // Định dạng màu sắc các nút Dialog cho đúng chủ đề của quán
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.parseColor("#D0770B")) // Màu cam thương hiệu
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.GRAY)
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }
}