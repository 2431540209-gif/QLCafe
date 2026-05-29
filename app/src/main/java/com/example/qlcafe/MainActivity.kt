package com.example.qlcafe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        loadFragment(FragmentTrangChu())
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // R.id.nav_home là ID của nút trong file menu XML nha, bạn nhớ check lại ID cho khớp
                R.id.nav_home -> {
                    loadFragment(FragmentTrangChu())
                    true
                }
                // Các nút khác tạm thời load lại Trang Chủ (Sau này bạn tạo FragmentTacVu thì thay vào đây)
                R.id.nav_tasks -> {
                    // loadFragment(FragmentTacVu())
                    true
                }
                else -> false
            }
        }
    }

    // Hàm thực hiện việc "rút băng, cắm băng" Fragment
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment) // frame_container là cái ID của FrameLayout bên XML
            .commit()
    }
}