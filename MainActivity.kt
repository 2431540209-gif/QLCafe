package com.example.qlcafe.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.qlcafe.R
import com.example.qlcafe.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Đây là layout chuẩn chứa 4 nút Menu
        setContentView(R.layout.layout_bottom_navigation)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Thiết lập màu nền hệ thống động theo chế độ Sáng/Tối
        val isNight = (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
        val mainRoot = findViewById<android.view.ViewGroup>(android.R.id.content).getChildAt(0)
        if (mainRoot != null) {
            val bgColor = if (isNight) android.graphics.Color.parseColor("#121212") else android.graphics.Color.parseColor("#F4F0EC")
            mainRoot.setBackgroundColor(bgColor)
        }

        // Đồng bộ màu sắc thanh Bottom Navigation theo chế độ Sáng/Tối
        if (isNight) {
            bottomNav.setBackgroundColor(android.graphics.Color.parseColor("#1E1E1E"))
            val tintColor = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E0E0E0"))
            bottomNav.itemIconTintList = tintColor
            bottomNav.itemTextColor = tintColor
        } else {
            bottomNav.setBackgroundColor(android.graphics.Color.WHITE)
            bottomNav.itemIconTintList = null // Khôi phục lại màu mặc định của Light Theme
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

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .commit()
    }
}
