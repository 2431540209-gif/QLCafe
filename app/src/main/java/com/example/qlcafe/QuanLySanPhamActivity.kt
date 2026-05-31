package com.example.qlcafe

import android.database.Cursor
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.qlcafe.R
import com.example.qlcafe.database.DatabaseHelper

class QuanLySanPhamActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var edtTenMon: EditText
    private lateinit var edtGiaMon: EditText
    private lateinit var btnThem: Button
    private lateinit var btnSua: Button
    private lateinit var btnXoa: Button
    private lateinit var lvSanPham: ListView
    private lateinit var btnBack: ImageButton // Thêm khai báo nút quay lại

    private var selectedId: Int = -1
    private lateinit var adapter: SimpleCursorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quan_ly_san_pham)

        dbHelper = DatabaseHelper(this)

        // Ánh xạ các View từ XML
        edtTenMon = findViewById(R.id.edtTenMon)
        edtGiaMon = findViewById(R.id.edtGiaMon)
        btnThem = findViewById(R.id.btnThem)
        btnSua = findViewById(R.id.btnSua)
        btnXoa = findViewById(R.id.btnXoa)
        lvSanPham = findViewById(R.id.lvSanPham)
        btnBack = findViewById(R.id.btnBack) // Ánh xạ nút quay lại

        hienThiDanhSach()

        // Xử lý sự kiện khi nhấn nút Quay Lại để về trang Tác vụ
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Đóng màn hình này và quay về FragmentTacVu
        }

        // Thêm sản phẩm
        btnThem.setOnClickListener {
            val ten = edtTenMon.text.toString().trim()
            val giaStr = edtGiaMon.text.toString().trim()

            if (ten.isEmpty() || giaStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            } else {
                val kq = dbHelper.insertSanPham(ten, giaStr.toDouble())
                if (kq > -1) {
                    Toast.makeText(this, "Thêm thành công!", Toast.LENGTH_SHORT).show()
                    lamMoiForm()
                    hienThiDanhSach()
                }
            }
        }

        // Chọn item trên ListView để chuẩn bị Sửa/Xóa
        lvSanPham.onItemClickListener = AdapterView.OnItemClickListener { _, view, _, id ->
            selectedId = id.toInt()

            val tvTen = view.findViewById<TextView>(R.id.txtTenMon)
            val tvGia = view.findViewById<TextView>(R.id.txtGiaMon)

            edtTenMon.setText(tvTen.text.toString())

            val giaGoc = tvGia.text.toString()
                .replace(" VNĐ", "")
                .replace(".", "")
                .replace(",", "")
            edtGiaMon.setText(giaGoc)
        }

        // Sửa sản phẩm
        btnSua.setOnClickListener {
            val ten = edtTenMon.text.toString().trim()
            val giaStr = edtGiaMon.text.toString().trim()

            if (selectedId == -1) {
                Toast.makeText(this, "Vui lòng chọn 1 sản phẩm từ danh sách trước!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (ten.isEmpty() || giaStr.isEmpty()) {
                Toast.makeText(this, "Không được để trống thông tin sửa!", Toast.LENGTH_SHORT).show()
            } else {
                dbHelper.updateSanPham(selectedId, ten, giaStr.toDouble())
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                lamMoiForm()
                hienThiDanhSach()
            }
        }

        // Xóa sản phẩm
        btnXoa.setOnClickListener {
            if (selectedId == -1) {
                Toast.makeText(this, "Vui lòng chọn 1 sản phẩm cần xóa!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dbHelper.deleteSanPham(selectedId)
            Toast.makeText(this, "Đã xóa sản phẩm!", Toast.LENGTH_SHORT).show()
            lamMoiForm()
            hienThiDanhSach()
        }
    }

    // Đổ dữ liệu SQLite vào custom layout item_san_pham
    private fun hienThiDanhSach() {
        val cursor: Cursor = dbHelper.getAllSanPham()

        val fromColumns = arrayOf(DatabaseHelper.COL_TEN_MON, DatabaseHelper.COL_GIA)
        val toViews = intArrayOf(R.id.txtTenMon, R.id.txtGiaMon)

        adapter = SimpleCursorAdapter(
            this,
            R.layout.item_san_pham,
            cursor,
            fromColumns,
            toViews,
            0
        )

        adapter.setViewBinder { view, cursorData, _ ->
            if (view.id == R.id.txtGiaMon) {
                val giaMon = cursorData.getDouble(cursorData.getColumnIndexOrThrow(DatabaseHelper.COL_GIA))
                (view as TextView).text = "${String.format("%,.0f", giaMon)} VNĐ"
                return@setViewBinder true
            }
            false
        }

        lvSanPham.adapter = adapter
    }

    private fun lamMoiForm() {
        edtTenMon.text.clear()
        edtGiaMon.text.clear()
        selectedId = -1
    }
}