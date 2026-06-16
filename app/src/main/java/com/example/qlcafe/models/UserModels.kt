package com.example.qlcafe.models// Sửa lại đúng package của bạn

// 1. Khuôn để Gửi dữ liệu (username, password) lên PHP
data class LoginRequest(
    val username: String,
    val password: String
)

// 2. Khuôn để Nhận dữ liệu từ PHP về
data class LoginResponse(
    val status: String,
    val message: String,
    val data: UserInfo? // Có thể null nếu đăng nhập sai
)

// 3. Khuôn thông tin chi tiết của User (Khớp với các cột trong bảng users)
data class UserInfo(
    val id: Int,
    val username: String,
    val role: String
)
