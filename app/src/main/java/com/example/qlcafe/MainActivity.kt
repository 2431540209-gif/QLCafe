package com.example.qlcafe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_bottom_navigation)

        // Ánh xạ View
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Hiển thị Fragment Trang Chủ mặc định khi mới mở App
        if (savedInstanceState == null) {
            // Lúc này mới cho phép hiển thị Trang Chủ mặc định
            loadFragment(FragmentTrangChu())
        }

        // Lắng nghe sự kiện chuyển Tab (Giữ nguyên cấu trúc gốc của bạn)
        bottomNav.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null

            when (item.itemId) {
                R.id.nav_home -> {
                    selectedFragment = FragmentTrangChu()
                }
                R.id.nav_tasks -> {
                    selectedFragment = FragmentTacVu()
                }
                R.id.nav_notifications -> {
                    selectedFragment = FragmentThongBao()
                }
                R.id.nav_account -> {
                    selectedFragment = FragmentTrangChu()
                }
            }

            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_container, selectedFragment)
                    .commit()
                true
            } else {
                false
            }
        }
    }

    /**
     * Hàm nạp Fragment vào FrameLayout (frame_container)
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .commit()
    }
}