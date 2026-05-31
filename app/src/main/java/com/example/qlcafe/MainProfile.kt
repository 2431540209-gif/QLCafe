package com.example.qlcafe

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Đảm bảo tên file layout ở đây khớp với tên file XML của bạn
        setContentView(R.layout.activity_profile)

        val cardIds = listOf(
            R.id.cardPassword, R.id.cardStaff, R.id.cardInventory,
            R.id.cardReceipt, R.id.cardNotification, R.id.cardSettings
        )

        cardIds.forEach { id ->
            val card = findViewById<CardView>(id)
            card?.setOnClickListener {
                when (id) {
                    R.id.cardPassword -> showToast("Mở Đổi mật khẩu")
                    R.id.cardStaff -> showToast("Mở Quản lý nhân viên")
                    R.id.cardInventory -> showToast("Mở Kho vật tư")
                    R.id.cardReceipt -> showToast("Mở Hóa đơn")
                    R.id.cardNotification -> showToast("Mở Thông báo")
                    R.id.cardSettings -> showToast("Mở Cài đặt")
                }
            }
        }

        findViewById<Button>(R.id.btnLogout)?.setOnClickListener {
            showToast("Đã đăng xuất")
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}