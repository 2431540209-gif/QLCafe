package com.example.qlcafe.api

import com.example.qlcafe.models.AddAttendanceRequest
import com.example.qlcafe.models.AddAttendanceResponse
import com.example.qlcafe.models.AttendanceRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.qlcafe.models.ChamCongRequest
import com.example.qlcafe.models.ChamCongResponse
import com.example.qlcafe.models.ThongBao
import com.example.qlcafe.models.UpdateStatusRequest
import com.example.qlcafe.models.UpdateStatusResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @POST("register.php")
    fun registerUser(@Body request: RegisterRequest): Call<AuthResponse>

    @POST("login.php")
    fun loginUser(@Body request: LoginRequest): Call<AuthResponse>

    @POST("cham_cong.php")
    fun chamCongNhanVien(@Body request: ChamCongRequest): Call<ChamCongResponse>
    @GET("attendance_controller.php?action=get")
    fun getAttendanceRequests(@Query("user_id") userId: Int): Call<List<AttendanceRequest>>

    @POST("attendance_controller.php?action=add")
    fun addAttendanceRequest(@Body request: AddAttendanceRequest): Call<AddAttendanceResponse>

    @POST("attendance_controller.php?action=update")
    fun updateAttendanceStatus(@Body request: UpdateStatusRequest): Call<UpdateStatusResponse>

    // API Quản lý Thông Báo
    @GET("notification_controller.php?action=get")
    fun getNotifications(): Call<List<ThongBao>>

    @POST("notification_controller.php?action=add")
    fun addNotification(@Body request: ThongBao): Call<AddAttendanceResponse>

    @POST("notification_controller.php?action=delete")
    fun deleteNotification(@Body request: Map<String, Int>): Call<AddAttendanceResponse>
}
