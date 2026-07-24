package com.example.qlcafe.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.qlcafe.R
import com.example.qlcafe.auth.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.qlcafe.api.RetrofitClient
import com.example.qlcafe.database.DatabaseHelper
import com.example.qlcafe.models.ChamCongRequest
import com.example.qlcafe.models.ChamCongResponse
import com.example.qlcafe.models.ThongBao
import com.example.qlcafe.models.DashboardStatsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentTrangChu : Fragment(R.layout.fragment_main) {

    private lateinit var sessionManager: SessionManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Chuyển TextView thành biến toàn cục (như bản 1) để dễ update trong onResume
    private lateinit var tvSoDoanhThu: TextView
    private lateinit var tvSoDonHang: TextView

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            thucHienKiemTraDeChamCong()
        } else {
            Toast.makeText(requireContext(), "Bạn phải cấp quyền Vị trí mới chấm công được!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun daCapQuyenViTri(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val tvEmployeeName = view.findViewById<TextView>(R.id.tvEmployeeName)
        val tvRoleName = view.findViewById<TextView>(R.id.tvRoleName)
        val role = sessionManager.getUserRole()

        tvSoDoanhThu = view.findViewById(R.id.tvsodoanhthu)
        tvSoDonHang = view.findViewById(R.id.tvsodonhang)

        view.findViewById<TextView>(R.id.tvdoanhthu)?.text = "Doanh thu hôm nay"

        tvEmployeeName.text = sessionManager.getUserName()
        tvRoleName.text = role.replaceFirstChar { it.uppercase() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val btnChamCongHome = view.findViewById<Button>(R.id.btnChamCongHome)
        btnChamCongHome.setOnClickListener {
            if (daCapQuyenViTri()) {
                thucHienKiemTraDeChamCong()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
        // click vô thì nhảy sao doanh thu
        val cardDoanhThu = view.findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardDoanhThu)
        cardDoanhThu?.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, FragmentStatistic())
                .addToBackStack(null) // Cho phép bấm nút Back để quay lại Trang Chủ
                .commit()
        }
        loadThongBaoMoiNhatTrenTrangChu(view)
        loadDashboardStats()
    }

    // TỰ ĐỘNG LÀM MỚI SỐ LIỆU DOANH THU KHI MỞ LẠI TAB TRANG CHỦ
    override fun onResume() {
        super.onResume()
        loadDashboardStats()
    }

    private fun loadThongBaoMoiNhatTrenTrangChu(view: View) {
        val dbHelper = DatabaseHelper(requireContext())
        val cached = dbHelper.notificationDao.getCachedNotifications()

        // Hiển thị Cache trước cho nhanh
        if (cached.isNotEmpty()) {
            val tbMoiNhat = cached[0]
            view.findViewById<TextView>(R.id.tvNotifTarget).text = "Hệ thống"
            view.findViewById<TextView>(R.id.tvNotifTime).text = tbMoiNhat.created_at ?: "Vừa xong"
            view.findViewById<TextView>(R.id.tvNotifTitle).text = tbMoiNhat.title
            view.findViewById<TextView>(R.id.tvNotifContent).text = tbMoiNhat.short_content
        }

        // Gọi API ngầm để cập nhật dữ liệu mới
        RetrofitClient.instance.getNotifications().enqueue(object : Callback<List<ThongBao>> {
            override fun onResponse(call: Call<List<ThongBao>>, response: Response<List<ThongBao>>) {
                if (response.isSuccessful) {
                    val list = response.body()
                    if (!list.isNullOrEmpty()) {
                        dbHelper.notificationDao.cacheNotifications(list)
                        val tbMoiNhat = list[0]
                        view.findViewById<TextView>(R.id.tvNotifTarget).text = "Hệ thống"
                        view.findViewById<TextView>(R.id.tvNotifTime).text = tbMoiNhat.created_at ?: "Vừa xong"
                        view.findViewById<TextView>(R.id.tvNotifTitle).text = tbMoiNhat.title
                        view.findViewById<TextView>(R.id.tvNotifContent).text = tbMoiNhat.short_content
                    }
                }
            }
            override fun onFailure(call: Call<List<ThongBao>>, t: Throwable) {}
        })
    }

    private fun loadDashboardStats() {
        val dbHelper = DatabaseHelper(requireContext())
        val cachedStats = dbHelper.dashboardStatsDao.getCachedDashboardStats()

        // Hiển thị Cache trước cho mượt
        if (cachedStats != null) {
            tvSoDoanhThu.text = formatRevenue(cachedStats.total_revenue)
            tvSoDonHang.text = cachedStats.total_orders.toString()
        }

        // Fetch API để có số liệu real-time
        RetrofitClient.instance.getDashboardStats().enqueue(object : Callback<DashboardStatsResponse> {
            override fun onResponse(call: Call<DashboardStatsResponse>, response: Response<DashboardStatsResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        val stats = body.data
                        dbHelper.dashboardStatsDao.cacheDashboardStats(stats)
                        tvSoDoanhThu.text = formatRevenue(stats.total_revenue)
                        tvSoDonHang.text = stats.total_orders.toString()
                    }
                }
            }

            override fun onFailure(call: Call<DashboardStatsResponse>, t: Throwable) {
                // Có thể để trống, UI vẫn sẽ hiển thị dữ liệu từ cache
            }
        })
    }

    private fun formatRevenue(revenue: Double): String {
        return if (revenue >= 1_000_000) {
            String.format(Locale.US, "%.1fM", revenue / 1_000_000.0)
        } else if (revenue >= 1_000) {
            String.format(Locale.US, "%.1fk", revenue / 1_000.0)
        } else {
            String.format(Locale.US, "%.0f", revenue)
        }
    }

    @SuppressLint("MissingPermission")
    private fun thucHienKiemTraDeChamCong() {
        if (!kiemTraWiFiHopLe()) {
            Toast.makeText(requireContext(), "Lỗi: Không đúng Wi-Fi của quán!", Toast.LENGTH_SHORT).show()
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val viDoHienTai = location.latitude
                val kinhDoHienTai = location.longitude

                if (!kiemTraToaDoHopLe(viDoHienTai, kinhDoHienTai)) {
                    Toast.makeText(requireContext(), "Bạn đang đứng quá xa quán (>50m)!", Toast.LENGTH_SHORT).show()
                } else {
                    hienThiBangXacNhanChamCong()
                }
            } else {
                Toast.makeText(requireContext(), "Không lấy được vị trí. Vui lòng bật GPS!", Toast.LENGTH_LONG).show()
            }
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    private fun kiemTraWiFiHopLe(): Boolean {
        val context = requireContext().applicationContext
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo

        // Lấy tên và xóa dấu ngoặc kép thừa
        val tenWifiHienTai = wifiInfo.ssid.replace("\"", "")
        val tenWifiCuaQuan = "AndroidWifi"

        println("TEN WIFI THUC TE MAY DOC DUOC LA: [$tenWifiHienTai]")

        return tenWifiHienTai == tenWifiCuaQuan
    }

    private fun kiemTraToaDoHopLe(viDoHienTai: Double, kinhDoHienTai: Double): Boolean {
        val toaDoQuan = Location("Nhà Riêng").apply {
            latitude = 10.769329
            longitude = 106.649301
        }
        val toaDoNhanVien = Location("NhanVien").apply {
            latitude = viDoHienTai
            longitude = kinhDoHienTai
        }
        val khoangCach = toaDoNhanVien.distanceTo(toaDoQuan)
        return khoangCach <= 50f
    }

    private fun hienThiBangXacNhanChamCong() {
        val ten = sessionManager.getUserName()
        val chucVu = sessionManager.getUserRole().replaceFirstChar { it.uppercase() }

        val calendar = Calendar.getInstance()
        val dinhDangNgay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
        val dinhDangGio = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(calendar.time)

        val thongTinChamCong = """
            Nhân viên: $ten
            Chức vụ: $chucVu
            Ngày: $dinhDangNgay
            Giờ check-in: $dinhDangGio
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận chấm công")
            .setMessage(thongTinChamCong)
            .setPositiveButton("Đồng ý") { _, _ ->

                val userId = sessionManager.getUserId()
                if (userId != -1) {
                    val request = ChamCongRequest(userId)

                    RetrofitClient.instance.chamCongNhanVien(request).enqueue(object : Callback<ChamCongResponse> {
                        override fun onResponse(call: Call<ChamCongResponse>, response: Response<ChamCongResponse>) {
                            if (response.isSuccessful && response.body() != null) {
                                val body = response.body()!!
                                if (body.success) {
                                    Toast.makeText(requireContext(), body.message, Toast.LENGTH_LONG).show()
                                    // Bấm chấm công xong load lại để hiện thông báo mới
                                    view?.let { loadThongBaoMoiNhatTrenTrangChu(it) }
                                } else {
                                    Toast.makeText(requireContext(), "Lỗi: ${body.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<ChamCongResponse>, t: Throwable) {
                            Toast.makeText(requireContext(), "Lỗi mạng: Không thể kết nối!", Toast.LENGTH_LONG).show()
                        }
                    })
                } else {
                    Toast.makeText(requireContext(), "Lỗi: Không tìm thấy ID nhân viên!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}