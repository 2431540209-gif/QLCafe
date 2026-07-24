package com.example.qlcafe.repository

import com.example.qlcafe.api.RetrofitClient
import com.example.qlcafe.models.AddAttendanceResponse
import com.example.qlcafe.models.ThongBao
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationRepository {
    private val api = RetrofitClient.instance

    fun getNotifications(onResult: (Boolean, List<ThongBao>?, String) -> Unit) {
        api.getNotifications().enqueue(object : Callback<List<ThongBao>> {
            override fun onResponse(call: Call<List<ThongBao>>, response: Response<List<ThongBao>>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(true, response.body(), "Thành công")
                } else {
                    onResult(false, null, "Lỗi phản hồi từ máy chủ")
                }
            }

            override fun onFailure(call: Call<List<ThongBao>>, t: Throwable) {
                onResult(false, null, "Mất kết nối: ${t.message}")
            }
        })
    }

    fun addNotification(thongBao: ThongBao, onResult: (Boolean, String) -> Unit) {
        api.addNotification(thongBao).enqueue(object : Callback<AddAttendanceResponse> {
            override fun onResponse(call: Call<AddAttendanceResponse>, response: Response<AddAttendanceResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(response.body()!!.success, response.body()!!.message)
                } else {
                    onResult(false, "Lỗi phản hồi từ máy chủ")
                }
            }

            override fun onFailure(call: Call<AddAttendanceResponse>, t: Throwable) {
                onResult(false, "Mất kết nối: ${t.message}")
            }
        })
    }

    fun deleteNotification(id: Int, onResult: (Boolean, String) -> Unit) {
        val request = mapOf("id" to id)
        api.deleteNotification(request).enqueue(object : Callback<AddAttendanceResponse> {
            override fun onResponse(call: Call<AddAttendanceResponse>, response: Response<AddAttendanceResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(response.body()!!.success, response.body()!!.message)
                } else {
                    onResult(false, "Lỗi phản hồi từ máy chủ")
                }
            }

            override fun onFailure(call: Call<AddAttendanceResponse>, t: Throwable) {
                onResult(false, "Mất kết nối: ${t.message}")
            }
        })
    }
}
