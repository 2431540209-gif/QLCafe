package com.example.qlcafe.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qlcafe.activity.MainActivity
import com.example.qlcafe.R
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        // val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister) // Nhớ thêm ID này bên XML

        btnLogin.setOnClickListener {
            val inputUser = etUsername.text.toString().trim()
            val inputPass = etPassword.text.toString().trim()

            if (inputUser.isEmpty() || inputPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tài khoản và mật khẩu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ==== LẤY DỮ LIỆU TỪ BỘ NHỚ RA ĐỂ KIỂM TRA ====
            val sharedPreferences = getSharedPreferences("FakeDatabase", MODE_PRIVATE)
            val savedUser = sharedPreferences.getString("saved_username", "")
            val savedPass = sharedPreferences.getString("saved_password", "")
            val savedRole = sharedPreferences.getString("saved_role", "BARISTA")

            // So sánh
            if (inputUser == savedUser && inputPass == savedPass) {
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                // Chuyển sang Trang chủ (Dashboard) và "kẹp" thêm cái tên + chức vụ đem qua đó
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("NICKNAME", savedUser)
                intent.putExtra("ROLE", savedRole)
                startActivity(intent)
                finish() // Đóng trang Đăng nhập
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show()
            }
        }

        /* Bỏ comment đoạn này nếu bạn đã thêm nút chuyển qua Đăng ký
        tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        */
    }
}