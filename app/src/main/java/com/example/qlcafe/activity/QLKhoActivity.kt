package com.example.qlcafe.activity

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.adapter.NguyenLieuAdapter
import com.example.qlcafe.database.DatabaseHelper

class QLKhoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var rvNguyenLieu: RecyclerView
    private lateinit var adapter: NguyenLieuAdapter

    // Danh sách nguyên liệu mặc định để hiển thị trong Spinner
    private val danhSachNguyenLieu = arrayOf(
        "Cà phê hạt", "Sữa tươi", "Sữa đặc", "Đường",
        "Bột cacao", "Trà xanh", "Syrup vanilla", "Whipping cream"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qlkho)

        dbHelper = DatabaseHelper(this)

        // Khởi tạo các View
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val layoutNhapKho = findViewById<LinearLayout>(R.id.layoutNhapKho)
        val spaceButton = findViewById<Space>(R.id.spaceButton)
        val btnNhapKho = findViewById<Button>(R.id.btnNhapKho)
        val btnXuatKho = findViewById<Button>(R.id.btnXuatKho)

        rvNguyenLieu = findViewById(R.id.rvNguyenLieu)
        rvNguyenLieu.layoutManager = LinearLayoutManager(this)

        // Tải dữ liệu lên RecyclerView
        loadDanhSachKho()

        btnBack.setOnClickListener { finish() }

        // Nhận quyền từ màn hình đăng nhập
        val role = intent.getStringExtra("ROLE")
        if (role == "staff") {
            layoutNhapKho.visibility = View.GONE
            spaceButton.visibility = View.GONE
        } else {
            layoutNhapKho.visibility = View.VISIBLE
            spaceButton.visibility = View.VISIBLE
        }

        // Bắt sự kiện Click nút Nhập Kho
        btnNhapKho.setOnClickListener {
            showDialogNhapXuatKho(isNhapKho = true)
        }

        // Bắt sự kiện Click nút Xuất Kho
        btnXuatKho.setOnClickListener {
            showDialogNhapXuatKho(isNhapKho = false)
        }
    }

    private fun loadDanhSachKho() {
        val list = dbHelper.getAllNguyenLieu()
        adapter = NguyenLieuAdapter(list)
        rvNguyenLieu.adapter = adapter
    }

    private fun showDialogNhapXuatKho(isNhapKho: Boolean) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_nhap_xuat_kho)
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val tvTitle = dialog.findViewById<TextView>(R.id.tvDialogTitle)
        val spinnerNguyenLieu = dialog.findViewById<Spinner>(R.id.spinnerNguyenLieu)
        val edtSoLuong = dialog.findViewById<EditText>(R.id.edtSoLuong)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancelDialog)
        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirmDialog)

        // Thay đổi tiêu đề dựa trên hành động (Nhập hoặc Xuất)
        tvTitle.text = if (isNhapKho) "Nhập kho nguyên liệu" else "Xuất kho nguyên liệu"

        // Đổ dữ liệu vào Spinner
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            danhSachNguyenLieu
        )
        spinnerNguyenLieu.adapter = spinnerAdapter

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val selectedNguyenLieu = spinnerNguyenLieu.selectedItem.toString()
            val soLuongStr = edtSoLuong.text.toString()

            if (soLuongStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val soLuong = soLuongStr.toInt()

            if (soLuong <= 0) {
                Toast.makeText(this, "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isNhapKho) {
                // XỬ LÝ NHẬP KHO
                dbHelper.nhapKho(selectedNguyenLieu, soLuong)
                Toast.makeText(this, "Nhập kho thành công!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()

            } else {
                // XỬ LÝ XUẤT KHO
                if (soLuong > 5) {
                    Toast.makeText(this, "Chỉ được xuất tối đa 5 đơn vị mỗi lần!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val status = dbHelper.xuatKho(selectedNguyenLieu, soLuong)
                when (status) {
                    1 -> {
                        Toast.makeText(this, "Xuất kho thành công!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    -1 -> Toast.makeText(this, "Lỗi: Không đủ số lượng trong kho!", Toast.LENGTH_SHORT).show()
                    0 -> Toast.makeText(this, "Lỗi: Nguyên liệu chưa có trong kho!", Toast.LENGTH_SHORT).show()
                }
            }

            // Làm mới lại giao diện RecyclerView ngay lập tức
            loadDanhSachKho()
        }

        dialog.show()
    }
}