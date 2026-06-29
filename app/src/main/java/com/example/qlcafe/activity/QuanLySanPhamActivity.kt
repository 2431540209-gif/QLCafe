package com.example.qlcafe.activity

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.qlcafe.R
import com.example.qlcafe.database.DatabaseHelper
import com.example.qlcafe.utils.setupTopBar

class QuanLySanPhamActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var edtTenMon: EditText
    private lateinit var edtGiaMon: EditText
    private lateinit var edtMoTa: EditText        // Thêm mới
    private lateinit var imgProduct: ImageView    // Thêm mới
    private lateinit var btnChooseImg: Button    // Thêm mới
    private lateinit var btnThem: Button
    private lateinit var btnSua: Button
    private lateinit var btnXoa: Button
    private lateinit var lvSanPham: ListView
    private lateinit var btnBack: ImageView

    private var selectedId: Int = -1
    private var selectedImageUri: String = "" // Biến lưu đường dẫn ảnh dạng chuỗi
    private lateinit var adapter: SimpleCursorAdapter

    // Bộ công cụ mở thư viện ảnh hệ thống của Android
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri.toString()
            imgProduct.setImageURI(uri) // Hiển thị ảnh lên giao diện
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quan_ly_san_pham)
        setupTopBar("Quản lý Sản phẩm")

        dbHelper = DatabaseHelper(this)

        // Ánh xạ View
        edtTenMon = findViewById(R.id.edtTenMon)
        edtGiaMon = findViewById(R.id.edtGiaMon)
        edtMoTa = findViewById(R.id.edtMoTa)
        imgProduct = findViewById(R.id.imgProduct)
        btnChooseImg = findViewById(R.id.btnChooseImg)
        btnThem = findViewById(R.id.btnThem)
        btnSua = findViewById(R.id.btnSua)
        btnXoa = findViewById(R.id.btnXoa)
        lvSanPham = findViewById(R.id.lvSanPham)
        btnBack = findViewById(R.id.btnBack)

        hienThiDanhSach()

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Bắt sự kiện chọn ảnh
        btnChooseImg.setOnClickListener {
            imagePickerLauncher.launch("image/*") // Chỉ mở các file định dạng ảnh
        }

        // Thêm sản phẩm
        btnThem.setOnClickListener {
            val ten = edtTenMon.text.toString().trim()
            val giaStr = edtGiaMon.text.toString().trim()
            val moTa = edtMoTa.text.toString().trim()

            if (ten.isEmpty() || giaStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ tên và giá!", Toast.LENGTH_SHORT).show()
            } else {
                val kq = dbHelper.insertSanPham(ten, giaStr.toDouble(), moTa, selectedImageUri)
                if (kq > -1) {
                    Toast.makeText(this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show()
                    lamMoiForm()
                    hienThiDanhSach()
                }
            }
        }

        // Chọn item trên ListView để sửa/xóa
        lvSanPham.onItemClickListener = AdapterView.OnItemClickListener { _, view, _, id ->
            selectedId = id.toInt()

            val cursor = dbHelper.getAllSanPham()
            if (cursor.moveToPosition(lvSanPham.getPositionForView(view))) {
                // Đọc chính xác dữ liệu từ SQLite ra dựa vào vị trí click
                val ten = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TEN_MON))
                val gia = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GIA))
                val moTa = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MO_TA))
                val hinhAnh = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_HINH_ANH))

                edtTenMon.setText(ten)
                edtGiaMon.setText(String.format("%.0f", gia))
                edtMoTa.setText(moTa)

                selectedImageUri = hinhAnh
                if (hinhAnh.isNotEmpty()) {
                    imgProduct.setImageURI(Uri.parse(hinhAnh))
                } else {
                    imgProduct.setImageResource(android.R.drawable.ic_menu_gallery)
                }
            }
        }

        // Sửa sản phẩm
        btnSua.setOnClickListener {
            val ten = edtTenMon.text.toString().trim()
            val giaStr = edtGiaMon.text.toString().trim()
            val moTa = edtMoTa.text.toString().trim()

            if (selectedId == -1) {
                Toast.makeText(this, "Vui lòng chọn sản phẩm cần sửa!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (ten.isEmpty() || giaStr.isEmpty()) {
                Toast.makeText(this, "Thông tin không được bỏ trống!", Toast.LENGTH_SHORT).show()
            } else {
                dbHelper.updateSanPham(selectedId, ten, giaStr.toDouble(), moTa, selectedImageUri)
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
        edtMoTa.text.clear()
        imgProduct.setImageResource(android.R.drawable.ic_menu_gallery)
        selectedImageUri = ""
        selectedId = -1
    }
}