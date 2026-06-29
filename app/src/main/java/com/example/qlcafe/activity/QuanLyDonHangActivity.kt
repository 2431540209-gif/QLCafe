package com.example.qlcafe.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.qlcafe.R
import com.example.qlcafe.fragment.DanhSachDonHangFragment
import com.example.qlcafe.fragment.FragmentTaoDonHang
import com.example.qlcafe.viewmodel.OrderViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class QuanLyDonHangActivity : AppCompatActivity() {

    private lateinit var viewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quanlydh)

        // Khởi tạo ViewModel ở cấp độ Activity để tồn tại xuyên suốt các tab và khi quay lại từ Task
        viewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Xử lý Intent từ FragmentTacVu để mở đúng tab
        val startTab = intent.getStringExtra("START_TAB") ?: "LIST"
        
        if (savedInstanceState == null) {
            if (startTab == "CREATE") {
                loadFragment(FragmentTaoDonHang())
                bottomNav.selectedItemId = R.id.nav_tao_don
            } else {
                loadFragment(DanhSachDonHangFragment())
                bottomNav.selectedItemId = R.id.nav_danh_sach
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.nav_danh_sach -> DanhSachDonHangFragment()
                R.id.nav_tao_don -> FragmentTaoDonHang()
                else -> DanhSachDonHangFragment()
            }
            loadFragment(selectedFragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
