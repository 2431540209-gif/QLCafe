package com.example.qlcafe.api

// Dữ liệu Gửi lên (Request)
data class RegisterRequest(val username: String, val phone: String, val password: String)
data class LoginRequest(val phone: String, val password: String)

// Dữ liệu Trả về (Response)
data class AuthResponse(
        val success: Boolean,
        val message: String,
        val user: UserInfo? = null // Có thể rỗng nếu đăng nhập sai
)

data class UserInfo(
        val id: Int,
        val username: String,
        val phone: String,
        val birth: String,
        val role: String
)
