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
import com.example.qlcafe.models.*

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

    @POST("attendance_controller.php?action=edit")
    fun editAttendanceRequest(@Body request: EditAttendanceRequest): Call<AddAttendanceResponse>

    @POST("attendance_controller.php?action=delete")
    fun deleteAttendanceRequest(@Body request: DeleteAttendanceRequest): Call<AddAttendanceResponse>

    // API Quản lý Thông Báo
    @GET("notification_controller.php?action=get")
    fun getNotifications(): Call<List<ThongBao>>

    @POST("notification_controller.php?action=add")
    fun addNotification(@Body request: ThongBao): Call<AddAttendanceResponse>

    @POST("notification_controller.php?action=delete")
    fun deleteNotification(@Body request: Map<String, Int>): Call<AddAttendanceResponse>

    // API Quản lý Đơn Hàng (Sắp thêm vào server)
    @GET("order_controller.php?action=get_products")
    fun getProducts(): Call<List<Product>>

    @POST("order_controller.php?action=create_order")
    fun createOrder(@Body request: OrderRequest): Call<OrderResponse>

    @GET("order_controller.php?action=get_orders")
    fun getOrders(@Query("user_id") userId: Int): Call<List<Order>>

    @POST("order_controller.php?action=update_status")
    fun updateOrderStatus(@Body request: Map<String, String>): Call<OrderResponse>

    @GET("order_controller.php?action=get_dashboard_stats")
    fun getDashboardStats(): Call<DashboardStatsResponse>

    // API Quản lý Sản phẩm
    @GET("product_controller.php?action=get")
    fun getProductsList(): Call<List<Product>>

    @POST("product_controller.php?action=add")
    fun addProduct(@Body request: AddProductRequest): Call<ProductResponse>

    @POST("product_controller.php?action=update")
    fun updateProduct(@Body request: UpdateProductRequest): Call<ProductResponse>

    @POST("product_controller.php?action=delete")
    fun deleteProduct(@Body request: DeleteProductRequest): Call<ProductResponse>

    // API Quản lý Kho
    @GET("inventory_controller.php?action=get")
    fun getInventoryList(): Call<List<NguyenLieu>>

    @POST("inventory_controller.php?action=nhap")
    fun nhapKho(@Body request: NhapXuatKhoRequest): Call<InventoryResponse>

    @POST("inventory_controller.php?action=xuat")
    fun xuatKho(@Body request: NhapXuatKhoRequest): Call<InventoryResponse>
    //API quản lý hệ thống
    @GET("phan_quyen.php?action=get_permissions")
    fun getPermissions(@Query("phone") phone: String): Call<PermissionResponse>

    @POST("phan_quyen.php?action=update_permissions")
    fun updatePermissions(@Body request: UpdatePermissionRequest): Call<UpdatePermissionResponse>

    @GET("get_doanh_thu.php")
    fun getDoanhThu(): Call<StatictisResponse>
}

