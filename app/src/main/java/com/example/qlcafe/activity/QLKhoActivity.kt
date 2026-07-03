package com.example.qlcafe

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Space
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
class QLKhoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qlkho)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val layoutNhapKho =
            findViewById<LinearLayout>(R.id.layoutNhapKho)

        val spaceButton =
            findViewById<Space>(R.id.spaceButton)

        // Nhận quyền từ màn hình đăng nhập
        val role = intent.getStringExtra("ROLE")

        if (role == "staff") {

            // Nhân viên: chỉ hiện Xuất kho
            layoutNhapKho.visibility = View.GONE
            spaceButton.visibility = View.GONE

        } else {

            // Quản lý: hiện cả 2 nút
            layoutNhapKho.visibility = View.VISIBLE
            spaceButton.visibility = View.VISIBLE
        }
    }
}