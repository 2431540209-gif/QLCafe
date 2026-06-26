package com.example.qlcafe.models

data class ThongBao(
    val loai: String,       // Phân loại: "KHO", "SU_KIEN", "NHAN_SU"
    val title: String,      // Tiêu đề ngắn
    val content: String,    // Nội dung tóm tắt ở ngoài
    val chiTiet: String,    // Nội dung dài thò lò hiện bên trong Bottom Sheet
    val time: String
)