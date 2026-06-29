package com.example.qlcafe.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qlcafe.activity.MainActivity
import com.example.qlcafe.R
import com.example.qlcafe.repository.UserRepository
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {
    private val userRepository = UserRepository()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        // KIỂM TRA ĐĂNG NHẬP: Nếu đã có Session thì bay thẳng vào Trang Chủ
        if (sessionManager.isLoggedIn()) {
            goToMainActivity()
            return
        }

        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        // val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        btnLogin.setOnClickListener {
            val inputPhone = etUsername.text.toString().trim()
            val inputPass = etPassword.text.toString().trim()

            if (inputPhone.isEmpty() || inputPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số điện thoại và mật khẩu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // XÓA FAKE DATABASE VÀ GỌI API THẬT
            userRepository.login(inputPhone, inputPass) {
                isSuccess, message, userInfo -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                if (isSuccess && userInfo != null) {
                    // Đăng nhập đúng máy chủ -> Lưu vô Session
                    sessionManager.createLoginSession(userInfo.id, userInfo.username, userInfo.phone, userInfo.role)
                    goToMainActivity()
                }
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Hủy luôn màn hình Login để bấm nút Back điện thoại không quay lại được
    }
}