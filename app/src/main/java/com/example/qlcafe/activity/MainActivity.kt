package com.example.qlcafe.activity

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.qlcafe.R
import com.example.qlcafe.api.ApiService
import com.example.qlcafe.auth.SessionManager
import com.example.qlcafe.fragment.*
import com.example.qlcafe.models.PermissionResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_bottom_navigation)

        sessionManager = SessionManager(this)

        // Khởi tạo Retrofit kết nối tới Server API
        // Lưu ý: Nếu trong project bạn đã có class RetrofitClient (như ở FragmentTrangChu),
        // bạn có thể dùng RetrofitClient.instance thay vì khởi tạo chay ở đây để code tối ưu hơn.
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2/qlcafe_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Thiết lập màu nền hệ thống động theo chế độ Sáng/Tối
        val isNight = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val mainRoot = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)

        if (mainRoot != null) {
            val bgColor = if (isNight) Color.parseColor("#121212") else Color.parseColor("#F4F0EC")
            mainRoot.setBackgroundColor(bgColor)
        }

        // Đồng bộ màu sắc thanh Bottom Navigation
        if (isNight) {
            bottomNav.setBackgroundColor(Color.parseColor("#1E1E1E"))
            val tintColor = ColorStateList.valueOf(Color.parseColor("#E0E0E0"))
            bottomNav.itemIconTintList = tintColor
            bottomNav.itemTextColor = tintColor
        } else {
            bottomNav.setBackgroundColor(Color.WHITE)
            bottomNav.itemIconTintList = null
            bottomNav.itemTextColor = null
        }

        // Luôn mở Trang chủ khi khởi động
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_home
            loadFragment(FragmentTrangChu())
        }

        bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.nav_home -> FragmentTrangChu()
                R.id.nav_tasks -> FragmentTacVu()
                R.id.nav_notifications -> FragmentNotifications()
                R.id.nav_account -> FragmentProfile()
                else -> FragmentTrangChu()
            }
            loadFragment(selectedFragment)
            true
        }
    }

    // Tự động đồng bộ quyền từ CSDL khi app được mở lại
    override fun onResume() {
        super.onResume()
        dongBoQuyenTuServer()
    }

    private fun dongBoQuyenTuServer() {
        // ĐÃ FIX LỖI: Lấy chính xác số điện thoại (Phone) thay vì lấy nhầm chuỗi đặc quyền
        val userPhone = sessionManager.getUserPhone() ?: ""
        if (userPhone.isEmpty()) return

        apiService.getPermissions(userPhone).enqueue(object : Callback<PermissionResponse> {
            override fun onResponse(call: Call<PermissionResponse>, response: Response<PermissionResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    // Cập nhật lại chuỗi quyền mới nhất vào SessionManager
                    val dacQuyenMoi = response.body()?.dacQuyen ?: ""
                    sessionManager.saveUserExtraPermissions(dacQuyenMoi)
                }
            }

            override fun onFailure(call: Call<PermissionResponse>, t: Throwable) {
                // Có thể tắt Toast này đi trong môi trường release để tránh spam thông báo khi rớt mạng
                Toast.makeText(this@MainActivity, "Không thể cập nhật quyền hạn từ máy chủ", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .commit()
    }
}