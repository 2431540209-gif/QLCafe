package com.example.qlcafe.repository

import com.example.qlcafe.api.AuthResponse
import com.example.qlcafe.api.LoginRequest
import com.example.qlcafe.api.RegisterRequest
import com.example.qlcafe.api.RetrofitClient
import com.example.qlcafe.models.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository {
    private val api = RetrofitClient.instance

    fun register(
        username: String,
        phone: String,
        password: String,
        role: String,
        permissions: String,
        onResult: (Boolean, String) -> Unit
    ) {
        // Đã truyền đầy đủ role và permissions vào đối tượng request gửi đi
        val request = RegisterRequest(username, phone, password, role, permissions)

        api.registerUser(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()
                    if (loginResponse?.success == true) {
                        onResult(true, loginResponse.message ?: "Đăng ký thành công")
                    } else {
                        onResult(false, loginResponse?.message ?: "Đăng ký thất bại")
                    }
                } else {
                    onResult(false, "Lỗi phản hồi từ máy chủ")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                onResult(false, "Mất kết nối mạng: ${t.message}")
            }
        })
    }

    fun login(
        phone: String,
        password: String,
        onResult: (Boolean, String, UserInfo?) -> Unit
    ) {
        val request = LoginRequest(phone, password)

        api.loginUser(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    onResult(authResponse.success, authResponse.message ?: "", authResponse.user)
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