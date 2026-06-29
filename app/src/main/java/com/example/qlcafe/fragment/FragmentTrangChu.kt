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
import com.example.qlcafe.models.ChamCongRequest
import com.example.qlcafe.models.ChamCongResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentTrangChu : Fragment(R.layout.fragment_main) {

    private lateinit var sessionManager: SessionManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Bộ xử lý hiển thị bảng xin quyền Vị trí của Android
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            thucHienKiemTraDeChamCong()
        } else {
            Toast.makeText(requireContext(), "Bạn phải cấp quyền Vị trí mới chấm công được!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Khởi tạo dữ liệu người dùng
        sessionManager = SessionManager(requireContext())
        val tvEmployeeName = view.findViewById<TextView>(R.id.tvEmployeeName)
        val tvRoleName = view.findViewById<TextView>(R.id.tvRoleName)

        tvEmployeeName.text = sessionManager.getUserName()
        val role = sessionManager.getUserRole()
        tvRoleName.text = role.replaceFirstChar { it.uppercase() }

        // 2. Khởi tạo công cụ đo GPS
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // 3. Xử lý nút Chấm công
        val btnChamCongHome = view.findViewById<Button>(R.id.btnChamCongHome)
        btnChamCongHome.setOnClickListener {
            // Kiểm tra xem đã có quyền Vị trí chưa
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Có quyền rồi thì bắt đầu quét Wi-Fi và GPS
                thucHienKiemTraDeChamCong()
            } else {
                // Chưa có quyền thì hiện bảng xin quyền
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    @SuppressLint("MissingPermission") // Bỏ qua cảnh báo lỗi đỏ của Android Studio vì mình đã check quyền ở trên rồi
    private fun thucHienKiemTraDeChamCong() {
        // BƯỚC 1: KIỂM TRA WI-FI
        if (!kiemTraWiFiHopLe()) {
            Toast.makeText(requireContext(), "Lỗi: Không đúng Wi-Fi của quán!", Toast.LENGTH_SHORT).show()
            return // Dừng lại, không chạy tiếp xuống dưới
        }

        // BƯỚC 2: KIỂM TRA GPS
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val viDoHienTai = location.latitude
                val kinhDoHienTai = location.longitude

                if (!kiemTraToaDoHopLe(viDoHienTai, kinhDoHienTai)) {
                    Toast.makeText(requireContext(), "Bạn đang đứng quá xa quán (>50m)!", Toast.LENGTH_SHORT).show()
                } else {
                    // Vượt qua vòng Wi-Fi và GPS -> Hiện bảng chốt chấm công
                    hienThiBangXacNhanChamCong()
                }
            } else {
                Toast.makeText(requireContext(), "Không lấy được vị trí. Vui lòng bật GPS (Vị trí) trên điện thoại!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun kiemTraWiFiHopLe(): Boolean {
        val wifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val tenWifiHienTai = wifiInfo.ssid.replace("\"", "") // Xóa dấu ngoặc kép thừa

        // Toast này để bạn xem máy đang đọc được tên mạng là gì
        //Toast.makeText(requireContext(), "Mạng đang đọc được: [$tenWifiHienTai]", Toast.LENGTH_LONG).show()

        // TODO: Chú ý - Tui đang để <unknown ssid> để bạn test trên máy ảo không bị lỗi.
        // Khi nào cài ra điện thoại thật, bạn hãy sửa lại thành tên Wi-Fi nhà bạn nha!
        val tenWifiCuaQuan = "AndroidWifi"

        return tenWifiHienTai == tenWifiCuaQuan
    }

    private fun kiemTraToaDoHopLe(viDoHienTai: Double, kinhDoHienTai: Double): Boolean {
        // Tọa độ của Quán Cafe (Đang lấy mẫu một vị trí)
        val toaDoQuan = Location("Nhà Riêng").apply {
            latitude = 10.769329
            longitude = 106.649301
        }

        // Tọa độ người dùng
        val toaDoNhanVien = Location("NhanVien").apply {
            latitude = viDoHienTai
            longitude = kinhDoHienTai
        }

        // Tính khoảng cách bằng mét
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

                // 1. Lấy ID nhân viên từ Session
                val userId = sessionManager.getUserId()

                if (userId != -1) {
                    // 2. Đóng gói dữ liệu
                    val request = ChamCongRequest(userId)

                    // 3. Gửi lên máy chủ bằng Retrofit
                    RetrofitClient.instance.chamCongNhanVien(request).enqueue(object : Callback<ChamCongResponse> {
                        override fun onResponse(call: Call<ChamCongResponse>, response: Response<ChamCongResponse>) {
                            if (response.isSuccessful && response.body() != null) {
                                val body = response.body()!!
                                if (body.success) {
                                    // Chấm công thành công
                                    Toast.makeText(requireContext(), body.message, Toast.LENGTH_LONG).show()
                                } else {
                                    // Chấm công thất bại (VD: Đã chấm rồi)
                                    Toast.makeText(requireContext(), "Lỗi: ${body.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<ChamCongResponse>, t: Throwable) {
                            Toast.makeText(requireContext(), "Lỗi mạng: Không thể kết nối tới máy chủ!", Toast.LENGTH_LONG).show()
                        }
                    })
                } else {
                    Toast.makeText(requireContext(), "Lỗi phiên đăng nhập: Không tìm thấy ID nhân viên!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}