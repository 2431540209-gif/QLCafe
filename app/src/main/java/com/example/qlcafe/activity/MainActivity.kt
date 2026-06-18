package com.example.qlcafe.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.qlcafe.R
import com.example.qlcafe.fragment.* // Đảm bảo import đúng gói chứa các fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_bottom_navigation)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        if (savedInstanceState == null) {
            loadFragment(FragmentTrangChu())
        }

        bottomNav.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null

            when (item.itemId) {
                R.id.nav_home -> selectedFragment = FragmentTrangChu()
                R.id.nav_tasks -> selectedFragment = FragmentTacVu()
                R.id.nav_notifications -> selectedFragment = FragmentThongBao()
                R.id.nav_account -> selectedFragment = FragmentProfile() // Đã trỏ đúng tới Profile
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment)
                true
            } else {
                false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .commit()
    }
}