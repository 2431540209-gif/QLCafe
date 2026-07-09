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
import com.example.qlcafe.R
import com.example.qlcafe.activity.AttendanceListActivity
import com.example.qlcafe.activity.QuanLyDonHangActivity
import com.example.qlcafe.activity.QuanLySanPhamActivity
import com.example.qlcafe.activity.QLKhoActivity
import com.example.qlcafe.activity.StaffManagementActivity
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

        rvMainTasks = view.findViewById(R.id.rvMainTasks)
        rvMainTasks.layoutManager = LinearLayoutManager(requireContext())

        sessionManager = SessionManager(requireContext())

        val userRole = sessionManager.getUserRole() ?: ""
        val dacQuyenString = sessionManager.getUserExtraPermissions() ?: ""

        val taskData = getTaskListByPermissions(userRole, dacQuyenString)

        val adapter = TaskCategoryAdapter(taskData, this)
        rvMainTasks.adapter = adapter

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        tvTitle?.text = "Tác vụ"
        val btnBack = view.findViewById<View>(R.id.btnBack)
        btnBack?.visibility = View.GONE

        return view
    }

    private fun getTaskListByPermissions(role: String, dacQuyenStr: String): List<TaskCategory> {
        val categories = mutableListOf<TaskCategory>()

        val listQuyen = dacQuyenStr.split(",").map { it.trim().lowercase() }
        val isManagerOrAdmin = role.equals("QUAN_LY", ignoreCase = true) || role.equals("ADMIN", ignoreCase = true)

        val orderTasks = mutableListOf<TaskItem>()

        if (isManagerOrAdmin || listQuyen.contains("tao_don_hang")) {
            orderTasks.add(TaskItem("tao_don_hang", "Tạo đơn hàng mới", android.R.drawable.ic_menu_add))
        }

        // Mục xem danh sách đơn hàng (Mặc định hiển thị cho tất cả)
        orderTasks.add(TaskItem("ds_don_hang", "Danh sách đơn hàng", android.R.drawable.ic_menu_view))

        if (orderTasks.isNotEmpty()) {
            categories.add(TaskCategory("Đơn hàng", orderTasks))
        }

        // 2. danh mục sản phẩm
        val productTasks = mutableListOf<TaskItem>()
        if (isManagerOrAdmin || listQuyen.contains("ql_san_pham")) {
            productTasks.add(TaskItem("ql_san_pham", "Quản lý sản phẩm", android.R.drawable.ic_menu_manage))
        }
        if (isManagerOrAdmin || listQuyen.contains("ql_kho")) {
            productTasks.add(TaskItem("ql_kho", "Quản lý kho nguyên liệu", android.R.drawable.ic_menu_save))
        }
        categories.add(TaskCategory("Sản phẩm", productTasks))

        // 3. Danh mục: lich làm việc
        val scheduleTasks = listOf(
            TaskItem("lich_chung", "Lịch làm việc chung", android.R.drawable.ic_menu_my_calendar),
            TaskItem("dang_ky_lich", "Đăng ký lịch làm việc", android.R.drawable.ic_menu_day),
            TaskItem("bo_sung_cong", "Bổ sung/ sửa chấm công", android.R.drawable.ic_menu_edit)
        )
        categories.add(TaskCategory("Lịch làm việc", scheduleTasks))

        // 4. danh mục quản lý hệ thống.
        if (isManagerOrAdmin || listQuyen.contains("ql_he_thong")) {
            val adminTasks = listOf(
                TaskItem("ql_nhan_vien", "Quản lý nhân viên", android.R.drawable.ic_menu_agenda),
                TaskItem("ql_hoa_don", "Báo cáo doanh thu", android.R.drawable.ic_menu_report_image)
            )
            categories.add(TaskCategory("Quản lý hệ thống", adminTasks))
        }

        return categories
    }

    override fun onTaskClick(item: TaskItem) {
        when (item.id) {
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
            "ql_san_pham" -> {
                val intent = Intent(requireContext(), QuanLySanPhamActivity::class.java)
                startActivity(intent)
            }
            "ql_kho" -> {
                val intent = Intent(requireContext(), QLKhoActivity::class.java)
                intent.putExtra("ROLE", sessionManager.getUserRole())
                startActivity(intent)
            }
            "xem_menu" -> {
                Toast.makeText(requireContext(), "Mở màn hình danh mục menu", Toast.LENGTH_SHORT).show()
            }
            "bo_sung_cong" -> {
                val intent = Intent(requireContext(), AttendanceListActivity::class.java)
                startActivity(intent)
            }
            "ql_nhan_vien" -> {
                val intent = Intent(requireContext(), StaffManagementActivity::class.java)
                startActivity(intent)
            }
            "dang_ky_lich" -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frame_container, FragmentDangKyLich())
                    .addToBackStack(null)
                    .commit()
            }
            "lich_chung" -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frame_container, FragmentLichChung())
                    .addToBackStack(null)
                    .commit()
            }
            "ql_hoa_don" -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frame_container, FragmentStatistic())
                    .addToBackStack(null)
                    .commit()
            }
            else -> {
                Toast.makeText(requireContext(), "Bạn vừa nhấn: ${item.title}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}