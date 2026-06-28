package com.example.qlcafe.repository

import com.example.qlcafe.api.AuthResponse
import com.example.qlcafe.api.LoginRequest
import com.example.qlcafe.api.RegisterRequest
import com.example.qlcafe.api.RetrofitClient
import com.example.qlcafe.api.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository {
    // Lấy cỗ máy Retrofit đã cài đặt sẵn
    private val api = RetrofitClient.instance

    // Hàm gọi API Đăng ký
    fun register(username: String, phone: String, password: String, onResult: (Boolean, String) -> Unit) {
        val request = RegisterRequest(username, phone, password)
        api.registerUser(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(response.body()!!.success, response.body()!!.message)
                } else {
                    onResult(false, "Lỗi phản hồi từ máy chủ")
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                onResult(false, "Mất kết nối mạng: ${t.message}")
            }
        })
    }

    // Hàm gọi API Đăng nhập
    fun login(phone: String, password: String, onResult: (Boolean, String, UserInfo?) -> Unit) {
        val request = LoginRequest(phone, password)
        api.loginUser(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    onResult(authResponse.success, authResponse.message, authResponse.user)
                } else {
                    onResult(false, "Lỗi phản hồi từ máy chủ", null)
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                onResult(false, "Mất kết nối mạng: ${t.message}", null)
            }
        })
    }
}