package com.example.qlcafe.activity // Đổi package sang thư mục activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
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

        adapter = AttendanceRequestAdapter(emptyList(), role) { requestId, newStatus ->
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
        }

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
}
