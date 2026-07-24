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
import com.example.qlcafe.api.RetrofitClient
import com.example.qlcafe.models.*
import com.example.qlcafe.utils.setupTopBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QLKhoActivity : AppCompatActivity() {

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
        setupTopBar("Quản Lý Kho")
        // Khởi tạo các View
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val layoutNhapKho = findViewById<LinearLayout>(R.id.layoutNhapKho)
        val spaceButton = findViewById<Space>(R.id.spaceButton)
        val btnNhapKho = findViewById<Button>(R.id.btnNhapKho)
        val btnXuatKho = findViewById<Button>(R.id.btnXuatKho)

        rvNguyenLieu = findViewById(R.id.rvNguyenLieu)
        rvNguyenLieu.layoutManager = LinearLayoutManager(this)

        // Khởi tạo adapter trống trước
        adapter = NguyenLieuAdapter(emptyList())
        rvNguyenLieu.adapter = adapter

        // Tải dữ liệu lên RecyclerView từ API
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
        RetrofitClient.instance.getInventoryList().enqueue(object : Callback<List<NguyenLieu>> {
            override fun onResponse(call: Call<List<NguyenLieu>>, response: Response<List<NguyenLieu>>) {
                if (response.isSuccessful && response.body() != null) {
                    adapter.updateData(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<NguyenLieu>>, t: Throwable) {
                Toast.makeText(this@QLKhoActivity, "Lỗi tải kho: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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

            val soLuong = soLuongStr.toIntOrNull()
            if (soLuong == null || soLuong <= 0) {
                Toast.makeText(this, "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnConfirm.isEnabled = false
            if (isNhapKho) {
                // XỬ LÝ NHẬP KHO QUA API
                val request = NhapXuatKhoRequest(selectedNguyenLieu, soLuong)
                RetrofitClient.instance.nhapKho(request).enqueue(object : Callback<InventoryResponse> {
                    override fun onResponse(call: Call<InventoryResponse>, response: Response<InventoryResponse>) {
                        btnConfirm.isEnabled = true
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(this@QLKhoActivity, "Nhập kho thành công!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            loadDanhSachKho()
                        } else {
                            val msg = response.body()?.message ?: "Lỗi nhập kho"
                            Toast.makeText(this@QLKhoActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<InventoryResponse>, t: Throwable) {
                        btnConfirm.isEnabled = true
                        Toast.makeText(this@QLKhoActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })

            } else {
                // XỬ LÝ XUẤT KHO QUA API
                if (soLuong > 5) {
                    Toast.makeText(this, "Chỉ được xuất tối đa 5 đơn vị mỗi lần!", Toast.LENGTH_SHORT).show()
                    btnConfirm.isEnabled = true
                    return@setOnClickListener
                }

                val request = NhapXuatKhoRequest(selectedNguyenLieu, soLuong)
                RetrofitClient.instance.xuatKho(request).enqueue(object : Callback<InventoryResponse> {
                    override fun onResponse(call: Call<InventoryResponse>, response: Response<InventoryResponse>) {
                        btnConfirm.isEnabled = true
                        if (response.isSuccessful) {
                            val body = response.body()
                            val status = body?.status ?: 0
                            when (status) {
                                1 -> {
                                    Toast.makeText(this@QLKhoActivity, "Xuất kho thành công!", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                    loadDanhSachKho()
                                }
                                -1 -> Toast.makeText(this@QLKhoActivity, "Lỗi: Không đủ số lượng trong kho!", Toast.LENGTH_SHORT).show()
                                else -> Toast.makeText(this@QLKhoActivity, "Lỗi: Nguyên liệu chưa có trong kho!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@QLKhoActivity, "Lỗi kết nối server!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<InventoryResponse>, t: Throwable) {
                        btnConfirm.isEnabled = true
                        Toast.makeText(this@QLKhoActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        dialog.show()
    }
}