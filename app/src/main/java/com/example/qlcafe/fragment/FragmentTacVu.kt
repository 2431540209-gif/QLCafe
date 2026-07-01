package com.example.qlcafe.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.activity.QuanLyDonHangActivity
import com.example.qlcafe.activity.QuanLySanPhamActivity
import com.example.qlcafe.QLKhoActivity
import com.example.qlcafe.R
import com.example.qlcafe.activity.AttendanceListActivity
import com.example.qlcafe.adapter.TaskCategoryAdapter
import com.example.qlcafe.adapter.TaskChildAdapter
import com.example.qlcafe.auth.SessionManager
import com.example.qlcafe.models.TaskCategory
import com.example.qlcafe.models.TaskItem

class FragmentTacVu : Fragment(), TaskChildAdapter.OnTaskClickListener {

    private lateinit var rvMainTasks: RecyclerView
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task, container, false)

        // Ánh xạ RecyclerView chính
        rvMainTasks = view.findViewById(R.id.rvMainTasks)
        rvMainTasks.layoutManager = LinearLayoutManager(requireContext())

        // Khởi tạo bộ quản lý phiên đăng nhập
        sessionManager = SessionManager(requireContext())

        // Lấy vai trò (role) hiện tại của người dùng
        val userRole = sessionManager.getUserRole()

        // Lấy danh sách công việc đã sắp xếp theo thứ tự ưu tiên
        val taskData = getTaskListByRole(userRole)

        // Nạp dữ liệu vào Adapter
        val adapter = TaskCategoryAdapter(taskData, this)
        rvMainTasks.adapter = adapter

        // Setup Top Bar
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        tvTitle?.text = "Tác vụ"
        val btnBack = view.findViewById<View>(R.id.btnBack)
        btnBack?.visibility = View.GONE

        // Setup Order Section
        view.findViewById<View>(R.id.btnTaoDonMoiSection)?.setOnClickListener {
            val intent = Intent(requireContext(), QuanLyDonHangActivity::class.java)
            intent.putExtra("START_TAB", "CREATE")
            startActivity(intent)
        }
        view.findViewById<View>(R.id.btnDsDonHangSection)?.setOnClickListener {
            val intent = Intent(requireContext(), QuanLyDonHangActivity::class.java)
            intent.putExtra("START_TAB", "LIST")
            startActivity(intent)
        }

        return view
    }

    /**
     * Hàm phân quyền và sắp xếp thứ tự danh mục tác vụ
     */
    private fun getTaskListByRole(role: String): List<TaskCategory> {
        val categories = mutableListOf<TaskCategory>()

        // 1. NHÓM SẢN PHẨM
        val productTasks = mutableListOf(
            TaskItem("xem_menu", "Danh mục thực đơn", android.R.drawable.ic_menu_view)
        )
        if (role == "manager") {
            productTasks.add(
                TaskItem(
                    "ql_san_pham",
                    "Quản lý sản phẩm",
                    android.R.drawable.ic_menu_manage
                )
            )
            productTasks.add(
                TaskItem(
                    "ql_kho",
                    "Quản lý kho nguyên liệu",
                    android.R.drawable.ic_menu_save
                )
            )
        }
        categories.add(TaskCategory("Sản phẩm", productTasks))

        // 4. NHÓM LỊCH LÀM VIỆC
        val scheduleTasks = listOf(
            TaskItem("lich_chung", "Lịch làm việc chung", android.R.drawable.ic_menu_my_calendar),
            TaskItem("dang_ky_lich", "Đăng ký lịch làm việc", android.R.drawable.ic_menu_day),
            TaskItem("bo_sung_cong", "Bổ sung/ sửa chấm công", android.R.drawable.ic_menu_edit)
        )
        categories.add(TaskCategory("Lịch làm việc", scheduleTasks))

        // 5. NHÓM QUẢN LÝ HỆ THỐNG
        if (role == "manager") {
            val adminTasks = listOf(
                TaskItem("ql_nhan_vien", "Quản lý nhân viên", android.R.drawable.ic_menu_agenda),
                TaskItem("ql_hoa_don", "Thống kê hóa đơn", android.R.drawable.ic_menu_report_image)
            )
            categories.add(TaskCategory("Quản lý hệ thống", adminTasks))
        }

        return categories
    }

    /**
     * Xử lý sự kiện click vào từng ô tác vụ con
     */
    override fun onTaskClick(item: TaskItem) {
        when (item.id) {
            "ql_san_pham" -> {
                val intent = Intent(requireContext(), QuanLySanPhamActivity::class.java)
                startActivity(intent)
            }
            "ql_kho" ->{
                // Mở màn hình Quản lí kho
                val intent = Intent(requireContext(), QLKhoActivity::class.java)
                // Truyền quyền của người dùng sang màn hình quản lí kho
                intent.putExtra("ROLE", sessionManager.getUserRole())

                startActivity(intent)
            }
            "tao_don_hang" -> {
                val intent = Intent(requireContext(), QuanLyDonHangActivity::class.java)
                intent.putExtra("START_TAB", "CREATE")
                startActivity(intent)
            }
            "ds_don_hang" -> {
                val intent = Intent(requireContext(), QuanLyDonHangActivity::class.java)
                intent.putExtra("START_TAB", "LIST")
                startActivity(intent)
            }
            "xem_menu" -> {
                Toast.makeText(requireContext(), "Mở màn hình danh mục menu", Toast.LENGTH_SHORT).show()
            }
            "bo_sung_cong" ->{
                val intent = Intent(requireContext(), AttendanceListActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Toast.makeText(requireContext(), "Bạn vừa nhấn: ${item.title}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}