package com.example.qlcafe.activity // Đổi package sang thư mục activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.utils.setupTopBar
import com.example.qlcafe.adapter.AttendanceRequestAdapter
import com.example.qlcafe.api.RetrofitClient
import com.example.qlcafe.auth.SessionManager
import com.example.qlcafe.models.AttendanceRequest
import com.example.qlcafe.models.UpdateStatusRequest
import com.example.qlcafe.models.UpdateStatusResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Kế thừa AppCompatActivity thay vì Fragment
class AttendanceListActivity : AppCompatActivity() {

    private lateinit var rvRequests: RecyclerView
    private lateinit var tvEmptyState: LinearLayout
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: AttendanceRequestAdapter
    private lateinit var sessionManager: SessionManager

    private var allRequests = mutableListOf<AttendanceRequest>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Tái sử dụng lại đúng file giao diện XML cũ, không cần thiết kế lại!
        setContentView(R.layout.fragment_attendance_request_list)
        setupTopBar("Danh sách yêu cầu") // Hàm của bạn giữ nguyên nè

        // Thay requireContext() thành chữ 'this'
        sessionManager = SessionManager(this)

        rvRequests = findViewById(R.id.rvAttendanceRequests)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        tabLayout = findViewById(R.id.tabLayoutRequests)
        val fabAddRequest = findViewById<FloatingActionButton>(R.id.fabThemBoSung)

        rvRequests.layoutManager = LinearLayoutManager(this)

        val role = sessionManager.getUserRole() // Lấy quyền của user

        adapter = AttendanceRequestAdapter(
            emptyList(),
            role,
            { requestId, newStatus ->
                // Bắn API khi quản lý bấm Duyệt/Từ chối
                val request = UpdateStatusRequest(requestId, newStatus)
                RetrofitClient.instance.updateAttendanceStatus(request).enqueue(object : Callback<UpdateStatusResponse> {
                    override fun onResponse(call: Call<UpdateStatusResponse>, response: Response<UpdateStatusResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(this@AttendanceListActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                            layDuLieuTuAPI() // Tự động load lại danh sách để thấy màu đổi
                        }
                    }
                    override fun onFailure(call: Call<UpdateStatusResponse>, t: Throwable) {
                        Toast.makeText(this@AttendanceListActivity, "Lỗi: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            },
            { don ->
                // Mở Dialog Sửa lịch làm việc
                openEditDialog(don)
            },
            { requestId ->
                // Mở Hộp thoại xác nhận Xóa lịch làm việc
                xacNhanXoaLich(requestId)
            }
        )

        rvRequests.adapter = adapter

        fabAddRequest.setOnClickListener {
            val intent = Intent(this, AddAttendanceRequestActivity::class.java)
            startActivity(intent)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                locDanhSachTheoTab(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        layDuLieuTuAPI()
    }

    private fun layDuLieuTuAPI() {
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        RetrofitClient.instance.getAttendanceRequests(userId).enqueue(object : Callback<List<AttendanceRequest>> {
            override fun onResponse(call: Call<List<AttendanceRequest>>, response: Response<List<AttendanceRequest>>) {
                if (response.isSuccessful && response.body() != null) {
                    allRequests.clear()
                    allRequests.addAll(response.body()!!)
                    locDanhSachTheoTab(tabLayout.selectedTabPosition)
                }
            }

            override fun onFailure(call: Call<List<AttendanceRequest>>, t: Throwable) {
                Toast.makeText(this@AttendanceListActivity, "Lỗi tải dữ liệu: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun locDanhSachTheoTab(viTriTab: Int) {
        val dsLoc = if (viTriTab == 0) {
            allRequests.filter { it.status == "pending" }
        } else {
            allRequests
        }

        adapter.updateData(dsLoc)

        if (dsLoc.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            rvRequests.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            rvRequests.visibility = View.VISIBLE
        }
    }

    private fun xacNhanXoaLich(requestId: Int) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Xóa yêu cầu lịch")
            .setMessage("Bạn có chắc chắn muốn xóa yêu cầu lịch làm việc này?")
            .setPositiveButton("Đồng ý") { _, _ ->
                val request = com.example.qlcafe.models.DeleteAttendanceRequest(requestId)
                RetrofitClient.instance.deleteAttendanceRequest(request).enqueue(object : Callback<com.example.qlcafe.models.AddAttendanceResponse> {
                    override fun onResponse(call: Call<com.example.qlcafe.models.AddAttendanceResponse>, response: Response<com.example.qlcafe.models.AddAttendanceResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(this@AttendanceListActivity, "Đã xóa lịch làm việc thành công!", Toast.LENGTH_SHORT).show()
                            layDuLieuTuAPI()
                        } else {
                            Toast.makeText(this@AttendanceListActivity, "Lỗi: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<com.example.qlcafe.models.AddAttendanceResponse>, t: Throwable) {
                        Toast.makeText(this@AttendanceListActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun openEditDialog(don: AttendanceRequest) {
        val dialog = android.app.Dialog(this)
        dialog.setContentView(R.layout.dialog_thao_tac)

        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val spinner = dialog.findViewById<android.widget.Spinner>(R.id.spinner_kho)
        val edt = dialog.findViewById<android.widget.EditText>(R.id.edt_input)
        val sw = dialog.findViewById<android.widget.Switch>(R.id.switch_notify)
        val lvNhanVien = dialog.findViewById<android.widget.ListView>(R.id.lv_nhan_vien)
        val btn = dialog.findViewById<android.widget.Button>(R.id.btn_xac_nhan)

        tvTitle.text = "Sửa lịch làm việc"
        
        spinner.visibility = View.GONE
        sw.visibility = View.GONE
        lvNhanVien.visibility = View.GONE

        edt.visibility = View.VISIBLE
        edt.hint = "Ghi chú (không bắt buộc)"
        edt.setText(don.note ?: "")

        val container = tvTitle.parent as LinearLayout
        
        var dateForDB = don.request_date
        val btnDate = android.widget.Button(this).apply {
            text = "Ngày: ${don.request_date}"
            setOnClickListener {
                val calendar = java.util.Calendar.getInstance()
                android.app.DatePickerDialog(this@AttendanceListActivity, { _, year, month, dayOfMonth ->
                    text = String.format("Ngày: %02d/%02d/%04d", dayOfMonth, month + 1, year)
                    dateForDB = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH)).show()
            }
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = 10
            }
        }
        container.addView(btnDate, 1)

        var selectedStart = don.start_time.substring(0, 5)
        var selectedEnd = don.end_time.substring(0, 5)

        val btnTime = android.widget.Button(this).apply {
            text = "Giờ: $selectedStart - $selectedEnd"
            setOnClickListener {
                val calendar = java.util.Calendar.getInstance()
                android.app.TimePickerDialog(this@AttendanceListActivity, { _, hour, minute ->
                    val startStr = String.format("%02d:%02d", hour, minute)
                    android.app.TimePickerDialog(this@AttendanceListActivity, { _, hour2, minute2 ->
                        val endStr = String.format("%02d:%02d", hour2, minute2)
                        selectedStart = startStr
                        selectedEnd = endStr
                        text = "Giờ: $selectedStart - $selectedEnd"
                    }, hour + 5, minute, true).show()
                }, 7, 0, true).show()
            }
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = 10
            }
        }
        container.addView(btnTime, 2)

        btn.text = "Cập nhật"
        btn.setOnClickListener {
            val noteVal = edt.text.toString().trim()
            val editReq = com.example.qlcafe.models.EditAttendanceRequest(
                don.id,
                don.user_id,
                don.branch,
                dateForDB,
                don.shift_name,
                selectedStart,
                selectedEnd,
                noteVal
            )

            btn.isEnabled = false
            btn.text = "Đang lưu..."

            RetrofitClient.instance.editAttendanceRequest(editReq).enqueue(object : Callback<com.example.qlcafe.models.AddAttendanceResponse> {
                override fun onResponse(call: Call<com.example.qlcafe.models.AddAttendanceResponse>, response: Response<com.example.qlcafe.models.AddAttendanceResponse>) {
                    btn.isEnabled = true
                    btn.text = "Cập nhật"
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@AttendanceListActivity, "Đã cập nhật lịch làm việc thành công!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        layDuLieuTuAPI()
                    } else {
                        Toast.makeText(this@AttendanceListActivity, "Lỗi: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<com.example.qlcafe.models.AddAttendanceResponse>, t: Throwable) {
                    btn.isEnabled = true
                    btn.text = "Cập nhật"
                    Toast.makeText(this@AttendanceListActivity, "Lỗi mạng: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        dialog.show()
    }
}
