package com.example.qlcafe.auth // Nhớ đổi tên package cho đúng với máy bạn

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qlcafe.R
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register) // File XML bạn vừa làm

        // 1. Ánh xạ các UI
        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPhone = findViewById<TextInputEditText>(R.id.etPhone)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        // 2. Bắt sự kiện bấm nút ĐĂNG KÝ
        btnRegister.setOnClickListener {
            val user = etUsername.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            // Kiểm tra xem có để trống ô nào không
            if (user.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Dừng lại, không chạy code bên dưới nữa
            }

            // ==== BÍ KÍP: LƯU TẠM VÀO BỘ NHỚ MÁY (Giả lập Database) ====
            val sharedPreferences = getSharedPreferences("FakeDatabase", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("saved_username", user)
            editor.putString("saved_password", pass)
            editor.putString("saved_role", "BARISTA") // Giả sử đăng ký mới mặc định là Barista
            editor.apply() // Lưu lại!

            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()

            // Chuyển về trang Đăng nhập
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Đóng trang đăng ký
        }

        // 3. Bắt sự kiện bấm chữ "Đã có tài khoản? Đăng nhập"
        tvGoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}