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

        // Ánh xạ RecyclerView chính
        rvMainTasks = view.findViewById(R.id.rvMainTasks)
        rvMainTasks.layoutManager = LinearLayoutManager(requireContext())

        // Khởi tạo bộ quản lý phiên đăng nhập
        sessionManager = SessionManager(requireContext())

        // Lấy vai trò và đặc quyền của người dùng (Đồng bộ với CSDL MySQL)
        val userRole = sessionManager.getUserRole() ?: ""
        val dacQuyenString = sessionManager.getUserExtraPermissions() ?: ""

        // Lấy danh sách danh mục tác vụ động dựa vào phân quyền
        val taskData = getTaskListByPermissions(userRole, dacQuyenString)

        // Nạp dữ liệu vào Adapter
        val adapter = TaskCategoryAdapter(taskData, this)
        rvMainTasks.adapter = adapter

        // Thiết lập tiêu đề cho thanh top bar
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        tvTitle?.text = "Tác vụ"
        val btnBack = view.findViewById<View>(R.id.btnBack)
        btnBack?.visibility = View.GONE

        return view
    }

    /**
     * Hàm phân quyền và sắp xếp thứ tự danh mục tác vụ (Sử dụng logic mới)
     */
    private fun getTaskListByPermissions(role: String, dacQuyenStr: String): List<TaskCategory> {
        val categories = mutableListOf<TaskCategory>()

        // Cắt chuỗi quyền thành danh sách, đưa về chữ thường và xóa khoảng trắng thừa
        val listQuyen = dacQuyenStr.split(",").map { it.trim().lowercase() }

        // Kiểm tra nếu thuộc cấp điều hành hệ thống (ADMIN hoặc QUAN_LY)
        val isManagerOrAdmin = role.equals("QUAN_LY", ignoreCase = true) || role.equals("ADMIN", ignoreCase = true)

        // =========================================================
        // 1. DANH MỤC: ĐƠN HÀNG
        // =========================================================
        val orderTasks = mutableListOf<TaskItem>()

        // Thỏa mãn là Quản lý/Admin HOẶC chuỗi đặc quyền chứa 'tao_don_hang'
        if (isManagerOrAdmin || listQuyen.contains("tao_don_hang")) {
            orderTasks.add(TaskItem("tao_don_hang", "Tạo đơn hàng mới", android.R.drawable.ic_menu_add))
        }

        // Mục xem danh sách đơn hàng (Mặc định hiển thị cho tất cả)
        orderTasks.add(TaskItem("ds_don_hang", "Danh sách đơn hàng", android.R.drawable.ic_menu_view))

        if (orderTasks.isNotEmpty()) {
            categories.add(TaskCategory("Đơn hàng", orderTasks))
        }

        // =========================================================
        // 2. DANH MỤC: SẢN PHẨM
        // =========================================================
        val productTasks = mutableListOf<TaskItem>()
        if (isManagerOrAdmin || listQuyen.contains("ql_san_pham")) {
            productTasks.add(TaskItem("ql_san_pham", "Quản lý sản phẩm", android.R.drawable.ic_menu_manage))
        }
        if (isManagerOrAdmin || listQuyen.contains("ql_kho")) {
            productTasks.add(TaskItem("ql_kho", "Quản lý kho nguyên liệu", android.R.drawable.ic_menu_save))
        }
        categories.add(TaskCategory("Sản phẩm", productTasks))

        // =========================================================
        // 3. DANH MỤC: LỊCH LÀM VIỆC
        // =========================================================
        val scheduleTasks = listOf(
            TaskItem("lich_chung", "Lịch làm việc chung", android.R.drawable.ic_menu_my_calendar),
            TaskItem("dang_ky_lich", "Đăng ký lịch làm việc", android.R.drawable.ic_menu_day),
            TaskItem("bo_sung_cong", "Bổ sung/ sửa chấm công", android.R.drawable.ic_menu_edit)
        )
        categories.add(TaskCategory("Lịch làm việc", scheduleTasks))

        // =========================================================
        // 4. DANH MỤC: QUẢN LÝ HỆ THỐNG
        // =========================================================
        if (isManagerOrAdmin || listQuyen.contains("ql_he_thong")) {
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