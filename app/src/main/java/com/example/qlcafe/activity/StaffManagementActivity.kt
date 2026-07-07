package com.example.qlcafe.activity

import android.content.ContentValues
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlcafe.R
import com.example.qlcafe.adapter.StaffAdapter
import com.example.qlcafe.database.DatabaseHelper
import com.example.qlcafe.models.UserInfo
import com.example.qlcafe.repository.UserRepository

class StaffManagementActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var userRepository: UserRepository
    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var spnRole: Spinner
    private lateinit var btnInsertUser: Button
    private lateinit var btnUpdateUser: Button
    private lateinit var rvEmployees: RecyclerView
    private lateinit var btnBack: ImageView
    private lateinit var cbPermissionOrder: CheckBox
    private lateinit var cbPermissionProduct: CheckBox
    private lateinit var cbPermissionWarehouse: CheckBox

    private var selectedUserPhone: String = ""
    private lateinit var staffAdapter: StaffAdapter
    private val staffList = mutableListOf<UserInfo>()
    private val rolesList = arrayOf("ADMIN", "QUAN_LY", "NHAN_VIEN_ORDER", "NHAN_VIEN_PHA_CHE")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_management)

        dbHelper = DatabaseHelper(this)
        userRepository = UserRepository()

        // Cập nhật tiêu đề Top Bar
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = "Quản lý nhân viên"

        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        spnRole = findViewById(R.id.spnRole)
        btnInsertUser = findViewById(R.id.btnInsertUser)
        btnUpdateUser = findViewById(R.id.btnUpdateUser)
        rvEmployees = findViewById(R.id.lvEmployees)
        btnBack = findViewById(R.id.btnBack)

        cbPermissionOrder = findViewById(R.id.cbPermissionOrder)
        cbPermissionProduct = findViewById(R.id.cbPermissionProduct)
        cbPermissionWarehouse = findViewById(R.id.cbPermissionWarehouse)

        rvEmployees.layoutManager = LinearLayoutManager(this)
        rvEmployees.isNestedScrollingEnabled = false

        staffAdapter = StaffAdapter(
            staffList = staffList,
            clickListener = { user ->
                selectedUserPhone = user.phone ?: ""
                setPermissionsToCheckBoxes(user.dacQuyen)

                edtUsername.setText(user.phone ?: user.username)
                edtPassword.setText("")

                val rolePosition = rolesList.indexOf(user.role)
                if (rolePosition != -1) {
                    spnRole.setSelection(rolePosition)
                }
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
        rvEmployees.adapter = staffAdapter

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, rolesList)
        spnRole.adapter = spinnerAdapter

        taiVaDongBoNhanVienTuServer()
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnInsertUser.setOnClickListener {
            val phone = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val role = spnRole.selectedItem.toString()
            val permissions = getSelectedPermissionsString()

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số điện thoại và mật khẩu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.PHONE.matcher(phone).matches()) {
                Toast.makeText(this, "Số điện thoại không hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dynamicUsername = "NV$phone"

            userRepository.register(dynamicUsername, phone, password, role, permissions) { isSuccess: Boolean, serverMessage: String ->
                if (isSuccess) {
                    val db = dbHelper.writableDatabase
                    val values = ContentValues()

                    values.put(DatabaseHelper.COL_USER_NAME, dynamicUsername)
                    values.put(DatabaseHelper.COL_USER_PHONE, phone)
                    values.put(DatabaseHelper.COL_USER_PASSWORD, password)
                    values.put(DatabaseHelper.COL_USER_ROLE, role)
                    values.put(DatabaseHelper.COL_USER_DAC_QUYEN, permissions)

                    try {
                        val result = db.insertOrThrow(DatabaseHelper.TABLE_USER, null, values)
                        if (result > -1) {
                            Toast.makeText(this, "Đã đồng bộ thành công lên hệ thống MySQL!", Toast.LENGTH_SHORT).show()
                            clearForm()
                            displayEmployeeList()
                        } else {
                            Toast.makeText(this, "Lỗi: Không thể lưu nhân viên vào SQLite cục bộ!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Lỗi SQLite Cục bộ: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Lỗi Server MySQL: $serverMessage", Toast.LENGTH_LONG).show()
                }
            }
        }

        btnUpdateUser.setOnClickListener {
            val phone = edtUsername.text.toString().trim()
            val role = spnRole.selectedItem.toString()
            val passwordNew = edtPassword.text.toString().trim()
            val permissions = getSelectedPermissionsString()

            if (selectedUserPhone.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn nhân viên từ danh sách để cập nhật!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (phone.isEmpty()) {
                Toast.makeText(this, "Số điện thoại không được để trống!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = dbHelper.writableDatabase
            val values = ContentValues()
            values.put(DatabaseHelper.COL_USER_PHONE, phone)
            values.put(DatabaseHelper.COL_USER_ROLE, role)
            values.put(DatabaseHelper.COL_USER_DAC_QUYEN, permissions)
            if (passwordNew.isNotEmpty()) {
                values.put(DatabaseHelper.COL_USER_PASSWORD, passwordNew)
            }

            val rows = db.update(DatabaseHelper.TABLE_USER, values, "${DatabaseHelper.COL_USER_PHONE} = ?", arrayOf(selectedUserPhone))
            if (rows > 0) {
                Toast.makeText(this, "Cập nhật thông tin nhân viên thành công!", Toast.LENGTH_SHORT).show()
                clearForm()
                displayEmployeeList()
            }
        }
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
        userRepository.deleteUserFromServer(phone) { isSuccess, serverMessage ->
            if (isSuccess) {
                val db = dbHelper.writableDatabase
                val rows = db.delete(DatabaseHelper.TABLE_USER, "${DatabaseHelper.COL_USER_PHONE} = ?", arrayOf(phone))
                if (rows > 0) {
                    Toast.makeText(this, "Đã xóa nhân viên trên cả Server và SQLite cục bộ!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Đã xóa trên Server nhưng không tìm thấy trong SQLite!", Toast.LENGTH_SHORT).show()
                }
                clearForm()
                displayEmployeeList()
            } else {
                Toast.makeText(this, "Lỗi khi xóa nhân viên trên Server: $serverMessage", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun taiVaDongBoNhanVienTuServer() {
        userRepository.getAllUsersFromServer { isSuccess, userList, message ->
            if (isSuccess && userList != null) {
                val db = dbHelper.writableDatabase
                // Xóa toàn bộ dữ liệu cũ trong bảng User để đồng bộ mới
                db.delete(DatabaseHelper.TABLE_USER, null, null)

                // Duyệt qua danh sách lấy từ Server và lưu vào SQLite Local
                for (user in userList) {
                    val values = ContentValues()
                    values.put(DatabaseHelper.COL_USER_ID, user.id)
                    values.put(DatabaseHelper.COL_USER_NAME, user.username)
                    values.put(DatabaseHelper.COL_USER_PHONE, user.phone)
                    values.put(DatabaseHelper.COL_USER_PASSWORD, "") // Không trả về mật khẩu từ API
                    values.put(DatabaseHelper.COL_USER_ROLE, user.role)
                    values.put(DatabaseHelper.COL_USER_DAC_QUYEN, user.dacQuyen ?: "")

                    try {
                        db.insert(DatabaseHelper.TABLE_USER, null, values)
                    } catch (e: Exception) {
                        // Bỏ qua nếu có lỗi dòng đơn lẻ
                    }
                }
                // Cập nhật giao diện sau khi đồng bộ thành công
                displayEmployeeList()
            } else {
                Toast.makeText(this, "Lỗi tải nhân viên từ Server: $message", Toast.LENGTH_SHORT).show()
                // Vẫn hiển thị từ SQLite local cũ làm phương án dự phòng
                displayEmployeeList()
            }
        }
    }

    private fun setPermissionsToCheckBoxes(permissionsStr: String?) {
        if (permissionsStr.isNullOrEmpty()) {
            cbPermissionOrder.isChecked = false
            cbPermissionProduct.isChecked = false
            cbPermissionWarehouse.isChecked = false
            return
        }
        val list = permissionsStr.split(",")
        cbPermissionOrder.isChecked = list.contains("tao_don_hang")
        cbPermissionProduct.isChecked = list.contains("ql_san_pham")
        cbPermissionWarehouse.isChecked = list.contains("ql_kho")
    }

    private fun getSelectedPermissionsString(): String {
        val list = mutableListOf<String>()
        if (cbPermissionOrder.isChecked) list.add("tao_don_hang")
        if (cbPermissionProduct.isChecked) list.add("ql_san_pham")
        if (cbPermissionWarehouse.isChecked) list.add("ql_kho")
        return list.joinToString(",")
    }

    private fun displayEmployeeList() {
        staffList.clear()
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DatabaseHelper.TABLE_USER}"
        val cursor = db.rawQuery(query, null)

        val idIdx = cursor.getColumnIndex(DatabaseHelper.COL_USER_ID)
        val nameIdx = cursor.getColumnIndex(DatabaseHelper.COL_USER_NAME)
        val phoneIdx = cursor.getColumnIndex(DatabaseHelper.COL_USER_PHONE)
        val roleIdx = cursor.getColumnIndex(DatabaseHelper.COL_USER_ROLE)
        val dacQuyenIdx = cursor.getColumnIndex(DatabaseHelper.COL_USER_DAC_QUYEN)

        if (cursor.moveToFirst()) {
            do {
                val id = if (idIdx != -1) cursor.getInt(idIdx) else 0
                val name = if (nameIdx != -1) cursor.getString(nameIdx) ?: "Nhân Viên" else "Nhân Viên"
                val phone = if (phoneIdx != -1) cursor.getString(phoneIdx) ?: "" else ""
                val role = if (roleIdx != -1) cursor.getString(roleIdx) ?: "" else ""
                val dacQuyen = if (dacQuyenIdx != -1) cursor.getString(dacQuyenIdx) ?: "" else ""

                staffList.add(UserInfo(id, name, phone, role, dacQuyen))
            } while (cursor.moveToNext())
        }
        cursor.close()
        staffAdapter.notifyDataSetChanged()
    }

    private fun clearForm() {
        edtUsername.text.clear()
        edtPassword.text.clear()
        spnRole.setSelection(0)
        cbPermissionOrder.isChecked = false
        cbPermissionProduct.isChecked = false
        cbPermissionWarehouse.isChecked = false
        selectedUserPhone = ""
    }
}