package com.example.qlcafe.api

import com.example.qlcafe.models.AddAttendanceRequest
import com.example.qlcafe.models.AddAttendanceResponse
import com.example.qlcafe.models.AttendanceRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.qlcafe.models.ChamCongRequest
import com.example.qlcafe.models.ChamCongResponse
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

    @POST("add_attendance_request.php")
    fun addAttendanceRequest(@Body request: AddAttendanceRequest): Call<AddAttendanceResponse>

    @POST("update_attendance_status.php")
    fun updateAttendanceStatus(@Body request: UpdateStatusRequest): Call<UpdateStatusResponse>
    @GET("get_attendance_requests.php")
    fun getAttendanceRequests(@Query("user_id") userId: Int): Call<List<AttendanceRequest>>

}
