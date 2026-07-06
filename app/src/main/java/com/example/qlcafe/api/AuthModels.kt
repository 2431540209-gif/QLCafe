package com.example.qlcafe.api

import com.google.gson.annotations.SerializedName
import com.example.qlcafe.models.UserInfo

// --- Dữ liệu Gửi lên (Request) ---

data class LoginRequest(
        @SerializedName("phone") val phone: String,
        @SerializedName("password") val password: String
)

data class RegisterRequest(
        @SerializedName("username") val username: String,
        @SerializedName("phone") val phone: String,
        @SerializedName("password") val password: String,
        @SerializedName("role") val role: String,
        @SerializedName("dac_quyen") val dacQuyen: String
)

// --- Dữ liệu Trả về (Response) ---

data class AuthResponse(
        @SerializedName("success") val success: Boolean,
        @SerializedName("message") val message: String?,
        @SerializedName("user") val user: UserInfo? = null
)