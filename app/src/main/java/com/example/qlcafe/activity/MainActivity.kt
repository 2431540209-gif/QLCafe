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


        // Luôn mở Trang chủ khi khởi động
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_home
            loadFragment(FragmentTrangChu())
        }

        bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.nav_home -> FragmentTrangChu()
                R.id.nav_tasks -> FragmentTacVu()
                R.id.nav_notifications -> FragmentThongBao()
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
