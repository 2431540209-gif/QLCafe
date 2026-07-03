package com.example.qlcafe.models


// Khuôn gửi lên (Chỉ cần ID nhân viên là đủ, giờ giấc PHP tự lo)
data class ChamCongRequest(
    val user_id: Int
)
data class AddAttendanceRequest(
    val user_id: Int,
    val branch: String,
    val request_date: String, // Định dạng chuẩn MySQL: YYYY-MM-DD
    val shift_name: String,
    val start_time: String,   // Định dạng chuẩn: HH:mm
    val end_time: String,
    val note: String
)
data class AttendanceRequest(
    val id: Int,
    val user_id: Int,
    val branch: String,
    val request_date: String,
    val shift_name: String,
    val start_time: String,
    val end_time: String,
    val note: String?,
    val status: String // "pending", "approved", "rejected"
)
data class UpdateStatusRequest(
    val request_id: Int,
    val new_status: String // Sẽ truyền "approved" hoặc "rejected"
)

// Khuôn nhận về từ PHP
data class ChamCongResponse(
    val success: Boolean,
    val message: String
)

data class AddAttendanceResponse(
    val success: Boolean,
    val message: String
)

data class UpdateStatusResponse(
    val success: Boolean,
    val message: String
)
data class EditAttendanceRequest(
    val id: Int,
    val user_id: Int,
    val branch: String,
    val request_date: String,
    val shift_name: String,
    val start_time: String,
    val end_time: String,
    val note: String
)

data class DeleteAttendanceRequest(
    val id: Int
)