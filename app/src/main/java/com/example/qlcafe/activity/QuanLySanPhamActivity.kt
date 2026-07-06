package com.example.qlcafe.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.qlcafe.R
import com.example.qlcafe.api.*
import com.example.qlcafe.models.*
import com.example.qlcafe.utils.setupTopBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuanLySanPhamActivity : AppCompatActivity() {

    private lateinit var edtTenMon: EditText
    private lateinit var edtGiaMon: EditText
    private lateinit var edtMoTa: EditText
    private lateinit var btnThem: Button
    private lateinit var btnSua: Button
    private lateinit var lvSanPham: ListView

    private var selectedId: Int = -1
    private var listProduct = mutableListOf<Product>()
    private lateinit var adapter: SanPhamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quan_ly_san_pham)
        setupTopBar("Quản lý Sản phẩm")

        // Ánh xạ View
        edtTenMon = findViewById(R.id.edtTenMon)
        edtGiaMon = findViewById(R.id.edtGiaMon)
        edtMoTa = findViewById(R.id.edtMoTa)
        btnThem = findViewById(R.id.btnThem)
        btnSua = findViewById(R.id.btnSua)
        lvSanPham = findViewById(R.id.lvSanPham)

        findViewById<ImageView>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        // Khởi tạo Adapter cho ListView
        adapter = SanPhamAdapter(this, listProduct)
        lvSanPham.adapter = adapter

        // Tải danh sách sản phẩm từ API
        hienThiDanhSach()

        // Thêm sản phẩm
        btnThem.setOnClickListener {
            val ten = edtTenMon.text.toString().trim()
            val giaStr = edtGiaMon.text.toString().trim()
            val moTa = edtMoTa.text.toString().trim()

            if (ten.isEmpty() || giaStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ tên và giá!", Toast.LENGTH_SHORT).show()
            } else {
                val gia = giaStr.toDoubleOrNull()
                if (gia == null) {
                    Toast.makeText(this, "Giá tiền không hợp lệ!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val request = AddProductRequest(ten, gia, moTa)
                btnThem.isEnabled = false
                RetrofitClient.instance.addProduct(request).enqueue(object : Callback<ProductResponse> {
                    override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                        btnThem.isEnabled = true
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(this@QuanLySanPhamActivity, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show()
                            lamMoiForm()
                            hienThiDanhSach()
                        } else {
                            val msg = response.body()?.message ?: "Lỗi thêm sản phẩm"
                            Toast.makeText(this@QuanLySanPhamActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                        btnThem.isEnabled = true
                        Toast.makeText(this@QuanLySanPhamActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        // Chọn item trên ListView để sửa
        lvSanPham.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val product = listProduct[position]
            selectedId = product.id

            edtTenMon.setText(product.name)
            edtGiaMon.setText(String.format("%.0f", product.price))
            edtMoTa.setText(product.description ?: "")
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
                val gia = giaStr.toDoubleOrNull()
                if (gia == null) {
                    Toast.makeText(this, "Giá tiền không hợp lệ!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val request = UpdateProductRequest(selectedId, ten, gia, moTa)
                btnSua.isEnabled = false
                RetrofitClient.instance.updateProduct(request).enqueue(object : Callback<ProductResponse> {
                    override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                        btnSua.isEnabled = true
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(this@QuanLySanPhamActivity, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                            lamMoiForm()
                            hienThiDanhSach()
                        } else {
                            val msg = response.body()?.message ?: "Lỗi cập nhật sản phẩm"
                            Toast.makeText(this@QuanLySanPhamActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                        btnSua.isEnabled = true
                        Toast.makeText(this@QuanLySanPhamActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        // Sự kiện đè (long click) để xóa sản phẩm
        lvSanPham.setOnItemLongClickListener { _, _, position, _ ->
            val productToDelete = listProduct[position]

            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm '${productToDelete.name}' không?")
                .setPositiveButton("Xóa") { _, _ ->
                    val request = DeleteProductRequest(productToDelete.id)
                    RetrofitClient.instance.deleteProduct(request).enqueue(object : Callback<ProductResponse> {
                        override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                            if (response.isSuccessful && response.body()?.success == true) {
                                Toast.makeText(this@QuanLySanPhamActivity, "Đã xóa sản phẩm!", Toast.LENGTH_SHORT).show()
                                lamMoiForm()
                                hienThiDanhSach()
                            } else {
                                val msg = response.body()?.message ?: "Lỗi xóa sản phẩm"
                                Toast.makeText(this@QuanLySanPhamActivity, msg, Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                            Toast.makeText(this@QuanLySanPhamActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                .setNegativeButton("Hủy", null)
                .show()

            true
        }
    }

    private fun hienThiDanhSach() {
        RetrofitClient.instance.getProductsList().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful && response.body() != null) {
                    listProduct.clear()
                    listProduct.addAll(response.body()!!)
                    adapter.updateData(listProduct)
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@QuanLySanPhamActivity, "Lỗi tải danh sách: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun lamMoiForm() {
        edtTenMon.text.clear()
        edtGiaMon.text.clear()
        edtMoTa.text.clear()
        selectedId = -1
    }

    // Adapter nội bộ để hiển thị danh sách sản phẩm
    private class SanPhamAdapter(private val context: Context, private var list: List<Product>) : BaseAdapter() {
        override fun getCount(): Int = list.size
        override fun getItem(position: Int): Any = list[position]
        override fun getItemId(position: Int): Long = list[position].id.toLong()
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_san_pham, parent, false)
            val product = list[position]

            val txtTenMon = view.findViewById<TextView>(R.id.txtTenMon)
            val txtGiaMon = view.findViewById<TextView>(R.id.txtGiaMon)

            txtTenMon.text = product.name
            txtGiaMon.text = "${String.format("%,.0f", product.price)} VNĐ"

            return view
        }

        fun updateData(newList: List<Product>) {
            this.list = newList
            notifyDataSetChanged()
        }
    }
}