package com.example.qlcafe.models
data class ThongBao(
    val id: Int = 0,
    val type: String,          // "INVENTORY", "EVENT", "HR"
    val title: String,         // Tiêu đề ngắn
    val short_content: String, // Nội dung tóm tắt
    val details: String?,      // Chi tiết dài
    val created_at: String? = null // Thời gian tạo từ MySQL
)