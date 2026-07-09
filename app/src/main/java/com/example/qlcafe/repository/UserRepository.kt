package com.example.qlcafe.repository

import com.example.qlcafe.api.AuthResponse
import com.example.qlcafe.api.LoginRequest
import com.example.qlcafe.api.RegisterRequest
import com.example.qlcafe.api.RetrofitClient
import com.example.qlcafe.models.UserInfo
import com.example.qlcafe.models.AddAttendanceResponse
import com.example.qlcafe.models.UpdatePermissionRequest
import com.example.qlcafe.models.UpdatePermissionResponse
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
        // truyền đầy đủ role và permissions vào đối tượng request gửi đi
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

    fun getAllUsersFromServer(onResult: (Boolean, List<UserInfo>?, String) -> Unit) {
        api.getUsers().enqueue(object : Callback<List<UserInfo>> {
            override fun onResponse(call: Call<List<UserInfo>>, response: Response<List<UserInfo>>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(true, response.body(), "Thành công")
                } else {
                    onResult(false, null, "Lỗi phản hồi từ máy chủ")
                }
            }

            override fun onFailure(call: Call<List<UserInfo>>, t: Throwable) {
                onResult(false, null, t.message ?: "Mất kết nối server")
            }
        })
    }

    fun deleteUserFromServer(phone: String, onResult: (Boolean, String) -> Unit) {
        val request = mapOf("phone" to phone)
        api.deleteUser(request).enqueue(object : Callback<AddAttendanceResponse> {
            override fun onResponse(call: Call<AddAttendanceResponse>, response: Response<AddAttendanceResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(response.body()!!.success, response.body()!!.message)
                } else {
                    onResult(false, "Lỗi phản hồi từ máy chủ")
                }
            }

            override fun onFailure(call: Call<AddAttendanceResponse>, t: Throwable) {
                onResult(false, t.message ?: "Mất kết nối server")
            }
        })
    }

    fun updatePermissionsFromServer(phone: String, permissions: String, onResult: (Boolean, String) -> Unit) {
        val request = UpdatePermissionRequest(phone, permissions)
        api.updatePermissions(request).enqueue(object : Callback<UpdatePermissionResponse> {
            override fun onResponse(
                call: Call<UpdatePermissionResponse>,
                response: Response<UpdatePermissionResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(response.body()!!.success, response.body()!!.message)
                } else {
                    onResult(false, "Lỗi phản hồi từ máy chủ")
                }
            }

            override fun onFailure(call: Call<UpdatePermissionResponse>, t: Throwable) {
                onResult(false, t.message ?: "Mất kết nối server")
            }
        })
    }
}