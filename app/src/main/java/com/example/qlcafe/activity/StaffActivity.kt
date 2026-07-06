package com.example.qlcafe.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.adapter.StaffAdapter
import com.example.qlcafe.database.DatabaseHelper
import com.example.qlcafe.models.UserInfo

class StaffActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var staffAdapter: StaffAdapter
    private val staffList = mutableListOf<UserInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Sử dụng kiến trúc giao diện XML chuẩn thay vì Programmatic UI
        setContentView(R.layout.activity_staff)

        dbHelper = DatabaseHelper(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        staffAdapter = StaffAdapter(
            staffList = staffList,
            clickListener = { user ->
                // Có thể mở rộng chức năng Xem/Sửa chi tiết nhân viên tại đây sau này
                Toast.makeText(this, "Đã chọn: ${user.username}", Toast.LENGTH_SHORT).show()
            },
            longClickListener = { user ->
                val userPhoneTarget = user.phone ?: ""
                if (userPhoneTarget.isNotEmpty()) {
                    hienThiCanhBaoXoaNhanVien(userPhoneTarget, user.username ?: "Nhân viên")
                } else {
                    Toast.makeText(this, "Không tìm thấy số điện thoại nhân viên để xóa!", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerView.adapter = staffAdapter

        docDanhSachNhanVienLocal()
    }

    private fun hienThiCanhBaoXoaNhanVien(phone: String, username: String) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa nhân viên $username ($phone) ra khỏi hệ thống?")
            .setPositiveButton("Xóa") { _, _ ->
                thucHienXoaNhanVienCucBo(phone)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun thucHienXoaNhanVienCucBo(phone: String) {
        val db = dbHelper.writableDatabase
        val rows = db.delete(DatabaseHelper.TABLE_USER, "${DatabaseHelper.COL_USER_PHONE} = ?", arrayOf(phone))
        if (rows > 0) {
            Toast.makeText(this, "Xóa tài khoản nhân viên thành công!", Toast.LENGTH_SHORT).show()
            // Tải lại danh sách sau khi xóa
            docDanhSachNhanVienLocal()
        } else {
            Toast.makeText(this, "Không thể xóa nhân viên trong cơ sở dữ liệu cục bộ!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun docDanhSachNhanVienLocal() {
        staffList.clear()
        val db = dbHelper.readableDatabase

        // Luôn sử dụng hằng số từ DatabaseHelper để đồng bộ và tránh sai lỗi chính tả cột
        val query = "SELECT ${DatabaseHelper.COL_USER_ID}, ${DatabaseHelper.COL_USER_NAME}, ${DatabaseHelper.COL_USER_PHONE}, ${DatabaseHelper.COL_USER_ROLE}, ${DatabaseHelper.COL_USER_DAC_QUYEN} FROM ${DatabaseHelper.TABLE_USER}"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME)) ?: "Nhân Viên"
                val phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PHONE)) ?: ""
                val role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ROLE)) ?: ""
                val dacQuyen = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_DAC_QUYEN)) ?: ""

                staffList.add(UserInfo(id, name, phone, role, dacQuyen))
            } while (cursor.moveToNext())
        }
        cursor.close()
        staffAdapter.notifyDataSetChanged()
    }
}